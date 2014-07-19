package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

import com.androidsx.rainnotifications.ForecastAnalyzer;
import com.androidsx.rainnotifications.Scheduler;
import com.androidsx.rainnotifications.model.WeatherObservable;
import com.androidsx.rainnotifications.util.AddressHelper;
import com.androidsx.rainnotifications.util.AnalyzerHelper;
import com.androidsx.rainnotifications.util.Constants;
import com.androidsx.rainnotifications.util.DateHelper;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;
import com.forecast.io.v2.transfer.DataPoint;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class WeatherService extends Service implements Observer {

    private static final String TAG = WeatherService.class.getSimpleName();

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int alarmID = 0;

    public WeatherObservable weatherObservable;

    public SharedPrefsHelper shared;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        shared = new SharedPrefsHelper(getApplicationContext());

        weatherObservable = new WeatherObservable();
        weatherObservable.addObserver(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle mBundle = intent.getExtras();
            if(mBundle != null) {
                double latitude = mBundle.getDouble(Constants.Extras.EXTRA_LAT, 1000);
                double longitude = mBundle.getDouble(Constants.Extras.EXTRA_LON, 1000);

                if (latitude != 1000 && longitude != 1000) {
                    weatherObservable.checkForecast(latitude, longitude);
                }
            }
        }
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass().equals(WeatherObservable.class)) {
            com.forecast.io.v2.network.services.ForecastService.Response response = (com.forecast.io.v2.network.services.ForecastService.Response) o;

            DataPoint currently = response.getForecast().getCurrently();

            String address = new AddressHelper().getLocationAddress(this,
                    response.getForecast().getLatitude(), response.getForecast().getLongitude());

            shared.setForecastAddress(address);

            ForecastAnalyzer fa = new ForecastAnalyzer();
            fa.setResponse(response);
            DataPoint dpRain = fa.analyzeForecastForRain(currently.getIcon());

            Intent mIntent = new Intent(this, WeatherService.class);
            Bundle mBundle = new Bundle();
            mBundle.putDouble(Constants.Extras.EXTRA_LAT, response.getForecast().getLatitude());
            mBundle.putDouble(Constants.Extras.EXTRA_LON, response.getForecast().getLongitude());
            mIntent.putExtras(mBundle);
            alarmIntent = PendingIntent.getService(getApplicationContext(), alarmID, mIntent, 0);

            if(alarmMgr != null) {
                Scheduler.setNextApiCallAlarm(alarmMgr, alarmIntent, dpRain.getTime() * 1000);
            }

            Log.d(TAG, "Weather Observer update...");

            if(dpRain != null && currently != null) {
                displayDebugResults(dpRain.getTime() * 1000, dpRain.getIcon(), currently.getIcon(), Constants.ForecastIO.Icon.RAIN);
            } else if(dpRain == null && currently != null) {
                displayDebugResults(0, "", currently.getIcon(), Constants.ForecastIO.Icon.RAIN);
            }
        }
        stopSelf();
    }

    private void displayDebugResults(long nextIconTime, String nextForecastIcon, String currentlyIcon, String searchingIcon) {

        String history = shared.getForecastHistory();

        String update = "";
        String currentTime = DateHelper
                .formatTime(System.currentTimeMillis(), Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_MADRID);
        String nextApiCall = DateHelper
                .formatTime(Scheduler.nextApiCallTime(nextIconTime), Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_MADRID);
        if(nextIconTime == 0) {
            update = "\nSearching: " + searchingIcon + "\n\nCurrently: " + currentlyIcon +
                    " at "+ currentTime +
                    "\n\nNo changes expected until tomorrow." +
                    "\n\nNext API call at: " + nextApiCall;
        }
        else {
            String deltaTime = DateHelper
                    .deltaTime(nextIconTime, System.currentTimeMillis());

            String forecastTime = DateHelper
                    .formatTime(nextIconTime, Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_MADRID);

            if(AnalyzerHelper.compareTo(nextForecastIcon, searchingIcon)) {
                update = "\nFound: " + nextForecastIcon + "\n\nCurrently: " + currentlyIcon +
                        "\nat "+ currentTime +
                        "\n\n" + nextForecastIcon + " expected at " + forecastTime +
                        " \n" + deltaTime + ".\n\nNext API call at: " + nextApiCall;
            } else {
                update = "\nSearching: " + searchingIcon + "\n\nCurrently: " + currentlyIcon +
                        "\nat "+ currentTime +
                        "\n\n" + nextForecastIcon + " expected at " + forecastTime +
                        " \n" + deltaTime + ".\n\nNext API call at: " + nextApiCall;
            }
        }
        history += update + "\n--------------------";
        if(nextIconTime == 0) {
            update = "No changes expected until tomorrow." +
                    "\n\nNext API call at: " + nextApiCall + "\n";
            shared.setCurrentForecastIcon(currentlyIcon);
            shared.setNextForecastIcon(currentlyIcon);
        }
        else {
            String deltaTime = DateHelper
                    .deltaTime(nextIconTime, System.currentTimeMillis());
            if(AnalyzerHelper.compareTo(nextForecastIcon, searchingIcon)) {
                update = deltaTime + ".\n\nNext API call at: " + nextApiCall + "\n";
            } else {
                update = deltaTime + ".\n\nNext API call at: " + nextApiCall + "\n";
            }
            shared.setCurrentForecastIcon(currentlyIcon);
            shared.setNextForecastIcon(nextForecastIcon);
        }
        Log.d(TAG, ".\n" + update);

        shared.setCurrentForecast(update);
        shared.setForecastHistory(history);

        String deltaTime = DateHelper
                .deltaTime(nextIconTime, System.currentTimeMillis());
        String expectedTime = DateHelper
                .formatTime(nextIconTime, Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_MADRID);
        if(!currentlyIcon.equals(Constants.ForecastIO.Icon.RAIN) && nextForecastIcon.equals(Constants.ForecastIO.Icon.RAIN)) {
            new NotificationHelper(this, "Rain expected " + deltaTime + " at " + expectedTime);
        } else if(currentlyIcon.equals(Constants.ForecastIO.Icon.RAIN) && !nextForecastIcon.equals(Constants.ForecastIO.Icon.RAIN)) {
            new NotificationHelper(this, "Stop raining expected " + deltaTime + " at " + expectedTime);
        }
    }
}
