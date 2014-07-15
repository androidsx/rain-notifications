package com.androidsx.rainnotifications.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;

import com.androidsx.rainnotifications.ForecastAnalyzer;
import com.androidsx.rainnotifications.Models.LocationObservable;
import com.androidsx.rainnotifications.Models.WeatherObservable;
import com.androidsx.rainnotifications.Utils.AddressHelper;
import com.androidsx.rainnotifications.Utils.AnalyzerHelper;
import com.androidsx.rainnotifications.Utils.Constants;
import com.androidsx.rainnotifications.Utils.DateHelper;
import com.androidsx.rainnotifications.Utils.NotificationHelper;
import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.DataPoint;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class WeatherService extends Service implements Observer {

    private static final String TAG = WeatherService.class.getSimpleName();
    public static final String SHARED_WEATHER = "weather";

    public static long nextApiCallTime = -1;

    public static Location lastLocation;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private LocationObservable locationObservable;
    public static WeatherObservable weatherObservable;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    double latitude = Constants.Localization.NEW_YORK_LAT;
    double longitude = Constants.Localization.NEW_YORK_LON;
    String forecast = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, ScheduleService.class);
        alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

        locationObservable =
                new LocationObservable(this, Constants.Localization.LOCATION_GPS_TIMEOUT,
                        Constants.Localization.LOCATION_NETWORK_TIMEOUT, Constants.Localization.LOCATION_DISTANCE);
        locationObservable.addObserver(this);

        weatherObservable = new WeatherObservable();
        weatherObservable.addObserver(this);

        lastLocation = new Location(LocationManager.NETWORK_PROVIDER);
        lastLocation.setLatitude(latitude);
        lastLocation.setLongitude(longitude);


        return START_STICKY;
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass().equals(LocationObservable.class)){
            Location location = (Location) o;

            //lastLocation = location;

            String address = new AddressHelper().getLocationAddress(this,
                    lastLocation.getLatitude(), lastLocation.getLongitude());

            weatherObservable.getWeather(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude());

            shared = getSharedPreferences(SHARED_WEATHER, 0);
            editor = shared.edit();

            editor.putString(Constants.SharedPref.LOCATION, address);
            editor.commit();

            Log.d(TAG, "Location Observer update...\nLocation: " + address +
                    " --> lat: " + latitude +
                    " - long: " + longitude);

        } else if(observable.getClass().equals(WeatherObservable.class)) {
            ForecastService.Response response = (ForecastService.Response) o;

            DataPoint currently = response.getForecast().getCurrently();
            DataPoint dpRain;

            ForecastAnalyzer fa = new ForecastAnalyzer();
            fa.setResponse(response);
            dpRain = fa.analyzeForecastForRain(currently.getIcon());

            if(alarmMgr != null) {
                alarmMgr.cancel(alarmIntent);
                alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() +
                                (nextApiCallTime * 1000 - System.currentTimeMillis()),
                        alarmIntent);
            }

            Log.d(TAG, "Weather Observer update..." +
                    "\n");

            String deltaTime = new DateHelper()
                    .deltaTime(dpRain.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            if(dpRain.getIcon().equals(Constants.ForecastIO.Icon.RAIN)) {
                new NotificationHelper(this, "Rain expected " + deltaTime);
            }

            writeResult(dpRain, currently, Constants.ForecastIO.Icon.RAIN);
        }
    }

    private void writeResult(DataPoint dp, DataPoint currently, String icon) {
        shared = getSharedPreferences(SHARED_WEATHER, 0);
        editor = shared.edit();
        forecast = shared.getString(Constants.SharedPref.HISTORY, "");

        String update = "";
        String currentTime = new DateHelper()
                .formatTime(System.currentTimeMillis() / 1000, Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_NEW_YORK, Locale.US);
        String nextApiCall = new DateHelper()
                .formatTime(nextApiCallTime, Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_NEW_YORK, Locale.US);
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
                    .formatTime(dp.getTime(), Constants.Time.TIME_FORMAT, Constants.Time.TIME_ZONE_NEW_YORK, Locale.US);

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
        Log.d(TAG, ".\n" + update);

        editor.putString(Constants.SharedPref.CURRENTLY, update);
        editor.putString(Constants.SharedPref.HISTORY, forecast);
        editor.commit();
    }
}
