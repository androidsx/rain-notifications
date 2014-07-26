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
import android.util.Log;

import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.util.AddressHelper;
import com.androidsx.rainnotifications.util.LocationHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;

import timber.log.Timber;

public class ForecastService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = ForecastService.class.getSimpleName();

    public static final String EXTRA_ALARM_TYPE = "extra_alarm_type"; // Location or Forecast
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    public static final int LOCATION_ALARM_ID = 0;
    public static final int FORECAST_ALARM_ID = 1;

    private static final double BAD_COORDINATE = 200;

    //Forecast
    private static final long FORECAST_REPEATING_TIME_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long TEN_MINUTES_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long TWO_HOUR_MILLIS = 2 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long DEFAULT_EXTRA_TIME_MILLIS = 2 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    //Location
    private static final float DEFAULT_DISTANCE = (float)5000.0;
    private static final long LOCATION_EXTRA_TIME_MILLIS = 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long LOCATION_REPEATING_TIME_MILLIS = 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long SPECIAL_LOCATION_EXTRA_TIME_MILLIS = 1 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long SPECIAL_LOCATION_REPEATING_TIME_MILLIS = 5 * DateTimeConstants.MILLIS_PER_MINUTE;

    private final AlertGenerator alertGenerator = new AlertGenerator();

    private Location lastLocation;
    private LocationClient mLocationClient;
    private PendingIntent locationAlarmIntent;
    private PendingIntent weatherAlarmIntent;

    private boolean goodCoordinatesReceived;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle mBundle = intent.getExtras();
            if(mBundle != null) {
                int alarmType = mBundle.getInt(EXTRA_ALARM_TYPE, -1); // Default -1 for indicate no valid alarm type.
                double latitude = mBundle.getDouble(EXTRA_LATITUDE, BAD_COORDINATE); // Default 200 for indicate no valid coordinate.
                double longitude = mBundle.getDouble(EXTRA_LONGITUDE, BAD_COORDINATE); // Default 200 for indicate no valid coordinate.

                locationAlarmIntent = PendingIntent.getService(this, LOCATION_ALARM_ID, intent, 0);
                weatherAlarmIntent = PendingIntent.getService(this, FORECAST_ALARM_ID, intent, 0);

                if(alarmType == LOCATION_ALARM_ID) {
                    goodCoordinatesReceived = false;
                    if(LocationHelper.rightCoordinates(latitude, longitude)) {
                        goodCoordinatesReceived = true;
                        lastLocation = new Location(LocationManager.NETWORK_PROVIDER);
                        lastLocation.setLatitude(latitude);
                        lastLocation.setLongitude(longitude);
                    }
                    mLocationClient = new LocationClient(this, this, this);
                    mLocationClient.connect();

                } else if(alarmType == FORECAST_ALARM_ID) {
                    if(LocationHelper.rightCoordinates(latitude, longitude)) {
                        callForecastAPI(latitude, longitude);
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mLocationClient.isConnected()) {
            Location loc = mLocationClient.getLastLocation();
            if(loc != null) {
                if (LocationHelper.isBetterLocation(loc, lastLocation)) {
                    updateForNewLocation(loc);
                }
            } else {
                // TODO: probably notify to user, that the gps is disabled or not available,
                // if we try to obtain many times the location.
                Bundle b = new Bundle();
                if(goodCoordinatesReceived) { // if good coordinates was received.
                    b.putDouble(EXTRA_LATITUDE, lastLocation.getLatitude());
                    b.putDouble(EXTRA_LONGITUDE, lastLocation.getLongitude());
                } else {
                    b.putDouble(EXTRA_LATITUDE, BAD_COORDINATE); // Bad location coordinates
                    b.putDouble(EXTRA_LONGITUDE, BAD_COORDINATE); // for simulate first call.
                }
                b.putInt(EXTRA_ALARM_TYPE, LOCATION_ALARM_ID);
                updateLocationAlarm(b, SPECIAL_LOCATION_EXTRA_TIME_MILLIS, SPECIAL_LOCATION_REPEATING_TIME_MILLIS);
            }
        }
    }

    private void updateForNewLocation(Location loc) {
        Bundle mBundle = new Bundle();
        mBundle.putDouble(EXTRA_LATITUDE, loc.getLatitude());
        mBundle.putDouble(EXTRA_LONGITUDE, loc.getLongitude());
        mBundle.putInt(EXTRA_ALARM_TYPE, LOCATION_ALARM_ID);

        // If ForecastService is called without extras, we call ForecastAPI with the location
        // and registers an alarm for be called again later with this location into extras.
        if(!goodCoordinatesReceived) {
            Timber.i("Init forecast process in %s (GPS %f, %f).", getAddress(loc.getLatitude(), loc.getLongitude()), loc.getLatitude(), loc.getLongitude());
            callForecastAPI(loc.getLatitude(), loc.getLongitude());
            updateLocationAlarm(mBundle, LOCATION_EXTRA_TIME_MILLIS, LOCATION_REPEATING_TIME_MILLIS);

        // Else, we compare the lastLocation with newest for determine if we call to ForecastService
        } else {
            if (loc.distanceTo(lastLocation) > DEFAULT_DISTANCE) {      // If new location is 5 km or more
                Timber.i("Restart forecast process in %s (GPS %f, %f).", getAddress(loc.getLatitude(), loc.getLongitude()), loc.getLatitude(), loc.getLongitude());
                callForecastAPI(loc.getLatitude(), loc.getLongitude()); // far to previous one, we restart the process.
                updateLocationAlarm(mBundle, LOCATION_EXTRA_TIME_MILLIS, LOCATION_REPEATING_TIME_MILLIS);

            } else {
                Timber.i("No location changes, nothing to do.");
            }
        }
    }

    private void callForecastAPI(final double latitude, final double longitude) {
        Timber.i("Ask forecast.io for the forecast in %s (GPS %f, %f).", getAddress(latitude, longitude), latitude, longitude);
        new ForecastIoNetworkServiceTask() {

            @Override
            protected void onSuccess(ForecastTable forecastTable) {
                // TODO: Here is where we should apply our logic
                Timber.i("Forecast table in %s (GPS %f, %f).\n%s", getAddress(latitude, longitude), latitude, longitude, forecastTable);

                Log.i(TAG, "We could generate the following alerts:");
                final Weather currentWeather = forecastTable.getBaselineWeather();
                for (Forecast forecast  : forecastTable.getForecasts()) {
                    final Alert alert = alertGenerator.generateAlert(currentWeather, forecast);
                    if (alert.getAlertLevel() == AlertLevel.INFO) {
                        Log.i(TAG, "INFO alert: " + alert.getAlertMessage());
                    }
                }

                Bundle mBundle = new Bundle();
                mBundle.putDouble(EXTRA_LATITUDE, latitude);
                mBundle.putDouble(EXTRA_LONGITUDE, longitude);
                mBundle.putInt(EXTRA_ALARM_TYPE, FORECAST_ALARM_ID);

                if(forecastTable.getForecasts().isEmpty()) {
                    updateWeatherAlarm(System.currentTimeMillis() + DEFAULT_EXTRA_TIME_MILLIS, mBundle);
                    Timber.i("There is no transition in %s (GPS %f, %f) expected in next days.",
                            getAddress(latitude, longitude), latitude, longitude);
                } else {
                    updateWeatherAlarm(forecastTable.getForecasts().get(0).getTimeFromNow().getEndMillis(), mBundle);
                    Timber.i("The next transition in %s (GPS %f, %f) is\n ---> %s",
                            getAddress(latitude, longitude), latitude, longitude, forecastTable.getForecasts().get(0));
                }
            }

            @Override
            protected void onFailure() {
                // TODO: And here is where we do something smart about failures
            }
        }.execute(new ForecastIoRequest(latitude, longitude).getRequest());
    }

    private void updateLocationAlarm(Bundle mBundle, long extraTime, long repeatingTime) {
        Timber.i("Schedule alarm for new location %s", new LocalTime(System.currentTimeMillis() + extraTime));
        locationAlarmIntent.cancel();
        locationAlarmIntent = PendingIntent.getService(
                this,
                LOCATION_ALARM_ID,
                new Intent(this, ForecastService.class).putExtras(mBundle),
                0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(am != null) {
            am.cancel(locationAlarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + extraTime,
                    repeatingTime,
                    locationAlarmIntent);
        }
    }

    private void updateWeatherAlarm(long expectedHour, Bundle mBundle) {
        Timber.i("Schedule alarm for update forecast %s", new LocalTime(nextWeatherCallAlarmTime(expectedHour)));
        weatherAlarmIntent.cancel();
        weatherAlarmIntent = PendingIntent.getService(
                this,
                FORECAST_ALARM_ID,
                new Intent(this, ForecastService.class).putExtras(mBundle),
                0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(am != null) {
            am.cancel(weatherAlarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    nextWeatherCallAlarmTime(expectedHour),
                    FORECAST_REPEATING_TIME_MILLIS,
                    weatherAlarmIntent);
        }
    }

    // That method is for determine the next time that we must call again to ForecastService.
    private long nextWeatherCallAlarmTime(long expectedHour) {
        final long currentTime = System.currentTimeMillis();
        if ((expectedHour - currentTime) < TEN_MINUTES_MILLIS) {
            return expectedHour;
        } else if (expectedHour - currentTime < TWO_HOUR_MILLIS){
            return currentTime + ((expectedHour - currentTime) * 70 / 100);
        } else {
            return currentTime + TWO_HOUR_MILLIS;
        }
    }

    private String getAddress(double latitude, double longitude) {
        return AddressHelper.getLocationAddress(this,
                latitude, longitude);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
