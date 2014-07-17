package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import com.androidsx.rainnotifications.ForecastAnalyzer;
import com.androidsx.rainnotifications.model.LocationObservable;
import com.androidsx.rainnotifications.model.WeatherObservable;
import com.androidsx.rainnotifications.util.AddressHelper;
import com.androidsx.rainnotifications.util.AnalyzerHelper;
import com.androidsx.rainnotifications.util.Constants;
import com.androidsx.rainnotifications.util.DateHelper;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;
import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.DataPoint;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class WeatherService extends Service implements Observer {

    private static final String TAG = WeatherService.class.getSimpleName();

    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LON = "extra_lon";

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private PendingIntent locationAlarmIntent;
    private Location lastLocation;

    private LocationObservable locationObservable;
    public WeatherObservable weatherObservable;

    public static SharedPrefsHelper sharedHelper;
    private ScheduleService scheduler;

    String forecast = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        sharedHelper = new SharedPrefsHelper(getApplicationContext());

        locationObservable = new LocationObservable(this,
                Constants.Localization.LOCATION_GPS_TIMEOUT,
                Constants.Localization.LOCATION_NETWORK_TIMEOUT,
                Constants.Localization.LOCATION_DISTANCE);
        locationObservable.addObserver(this);

        weatherObservable = new WeatherObservable();
        weatherObservable.addObserver(this);

        scheduler = new ScheduleService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            locationObservable.startLocationListeners();

        } else {
            locationObservable.stopLocationListeners();
            long nextAlarmTime = sharedHelper.getNextAlarmTime();
            alarmIntent = PendingIntent.getService(getApplicationContext(), 0, new Intent(this, ScheduleService.class), 0);
            scheduler.setNextApiCallAlarm(alarmMgr, alarmIntent, nextAlarmTime);
        }

        return START_STICKY;
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass().equals(LocationObservable.class)){
            Location location = (Location) o;

            String address = new AddressHelper().getLocationAddress(this,
                    location.getLatitude(), location.getLongitude());

            if(lastLocation == null) {
                lastLocation = location;
            }

            if(lastLocation.distanceTo(location) > Constants.Distance.KM * 5 || lastLocation.equals(location)) {
                weatherObservable.setRequest(location.getLatitude(), location.getLongitude());
                weatherObservable.getForecast(weatherObservable.getRequest());

                sharedHelper.setForecastAddress(address);
            }

            Intent mIntent = new Intent(this, ScheduleService.class);
            Bundle mBundle = new Bundle();
            mBundle.putDouble(EXTRA_LAT, location.getLatitude());
            mBundle.putDouble(EXTRA_LON, location.getLongitude());
            mIntent.putExtras(mBundle);
            locationAlarmIntent = PendingIntent.getService(getApplicationContext(), 0, mIntent, 0);

            long nextAlarmTime = System.currentTimeMillis() + Time.HOUR;
            if(alarmMgr != null) {
                scheduler.setNextLocationAlarm(alarmMgr, locationAlarmIntent, nextAlarmTime);
            }

            Log.d(TAG, "Location Observer update...\nLocation: " + address +
                    " --> lat: " + location.getLatitude() +
                    " - long: " + location.getLongitude());

        } else if(observable.getClass().equals(WeatherObservable.class)) {
            ForecastService.Response response = (ForecastService.Response) o;

            DataPoint currently = response.getForecast().getCurrently();

            ForecastAnalyzer fa = new ForecastAnalyzer();
            fa.setResponse(response);
            DataPoint dpRain = fa.analyzeForecastForRain(currently.getIcon());

            Intent mIntent = new Intent(this, ScheduleService.class);
            alarmIntent = PendingIntent.getService(getApplicationContext(), 0, mIntent, 0);

            if(alarmMgr != null) {
                scheduler.setNextApiCallAlarm(alarmMgr, alarmIntent, dpRain);
            }

            Log.d(TAG, "Weather Observer update..." +
                    "\n");

            displayDebugResults(dpRain, currently, Constants.ForecastIO.Icon.RAIN);
        }
    }

    private void displayDebugResults(DataPoint dp, DataPoint currently, String icon) {

        forecast = sharedHelper.getForecastHistory();

        String update = "";
        String currentTime = new DateHelper()
                .formatTime(System.currentTimeMillis() / 1000, Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_MADRID, Locale.US);
        String nextApiCall = new DateHelper()
                .formatTime(scheduler.nextApiCallTime(dp), Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_MADRID, Locale.US);
        if(dp == null) {
            update = "\nSearching: " + icon + "\n\nCurrently: " + currently.getIcon() +
                    " at "+ currentTime +
                    "\n\nNo changes expected until tomorrow." +
                    "\n\nNext API call at: " + nextApiCall;
        }
        else {
            String deltaTime = new DateHelper()
                    .deltaTime(dp.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);

            String forecastTime = new DateHelper()
                    .formatTime(dp.getTime(), Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_MADRID, Locale.US);

            if(AnalyzerHelper.compareTo(dp.getIcon(), icon)) {
                update = "\nFound: " + dp.getIcon() + "\n\nCurrently: " + currently.getIcon() +
                        "\nat "+ currentTime +
                        "\n\n" + dp.getIcon() + " expected at " + forecastTime +
                        " \n" + deltaTime + ".\n\nNext API call at: " + nextApiCall;
            } else {
                update = "\nSearching: " + icon + "\n\nCurrently: " + currently.getIcon() +
                        "\nat "+ currentTime +
                        "\n\n" + dp.getIcon() + " expected at " + forecastTime +
                        " \n" + deltaTime + ".\n\nNext API call at: " + nextApiCall;
            }
        }
        forecast += update + "\n--------------------";
        if(dp == null) {
            update = "No changes expected until tomorrow." +
                    "\n\nNext API call at: " + nextApiCall + "\n";
            sharedHelper.setCurrentForecastIcon(currently.getIcon());
            sharedHelper.setNextForecastIcon(currently.getIcon());
        }
        else {
            String deltaTime = new DateHelper()
                    .deltaTime(dp.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            if(AnalyzerHelper.compareTo(dp.getIcon(), icon)) {
                update = deltaTime + ".\n\nNext API call at: " + nextApiCall + "\n";
            } else {
                update = deltaTime + ".\n\nNext API call at: " + nextApiCall + "\n";
            }
            sharedHelper.setCurrentForecastIcon(currently.getIcon());
            sharedHelper.setNextForecastIcon(dp.getIcon());
        }
        Log.d(TAG, ".\n" + update);

        sharedHelper.setNextForecast(update);
        sharedHelper.setForecastHistory(forecast);

        String deltaTime = "";
        String expectedTime = "";
        if(dp != null && currently != null) {
            deltaTime = new DateHelper()
                    .deltaTime(dp.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            expectedTime = new DateHelper()
                    .formatTime(dp.getTime(), Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_MADRID, Locale.US);
            if(!currently.getIcon().equals(Constants.ForecastIO.Icon.RAIN) && dp.getIcon().equals(Constants.ForecastIO.Icon.RAIN)) {
                new NotificationHelper(this, "Rain expected " + deltaTime + " at " + expectedTime);
            } else if(currently.getIcon().equals(Constants.ForecastIO.Icon.RAIN) && !dp.getIcon().equals(Constants.ForecastIO.Icon.RAIN)) {
                new NotificationHelper(this, "Stop raining expected " + deltaTime + " at " + expectedTime);
            }
        }
    }
}
