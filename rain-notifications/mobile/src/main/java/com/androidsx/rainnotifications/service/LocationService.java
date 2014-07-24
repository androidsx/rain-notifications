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

import com.androidsx.rainnotifications.util.LocationHelper;
import com.androidsx.rainnotifications.util.AddressHelper;
import com.androidsx.rainnotifications.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.joda.time.LocalTime;

/**
 * This service is responsible of obtain the user location.
 *
 * Now, it starts first time by main activity (Call the Forecast API button).
 * In turn, also it's registered on OnBootReceiver, for be started when system boots.
 *
 * Once started, it obtain the user location and starts WeatherService with that location and
 * registers an alarm that will call it later (now 1 hora later) with location coordinates into extras.
 *
 * When LocationService is called again by the alarm, it receives the last location coordinates
 * into extras, and compares it with the new obtained location for determine
 * if WeatherService be called with the newest coordinates.
 * (It will restart the process because the new location is far of previous,
 * and the forecast will be different).
 */

public class LocationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = LocationService.class.getSimpleName();

    private Location lastLocation;
    private LocationClient mLocationClient;
    private PendingIntent locationAlarmIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle mBundle = intent.getExtras();
            if(mBundle != null) {
                locationAlarmIntent = PendingIntent.getService(this, Constants.AlarmId.LOCATION_ID, intent, 0);

                double latitude = mBundle.getDouble(Constants.Extras.EXTRA_LAT, 1000);
                double longitude = mBundle.getDouble(Constants.Extras.EXTRA_LON, 1000);

                if(LocationHelper.rightCoordinates(latitude, longitude)) {
                    lastLocation = new Location(LocationManager.NETWORK_PROVIDER);
                    lastLocation.setLatitude(latitude);
                    lastLocation.setLongitude(longitude);
                }
            }
        }
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateLocation(Location loc) {
        String address = AddressHelper.getLocationAddress(this,
                loc.getLatitude(), loc.getLongitude());

        Bundle mBundle = new Bundle();
        mBundle.putDouble(Constants.Extras.EXTRA_LAT, loc.getLatitude());
        mBundle.putDouble(Constants.Extras.EXTRA_LON, loc.getLongitude());

        // If LocationService is called without extras, we call WeatherService with the location
        // and registers an alarm for be called again later with this location into extras.
        if(lastLocation == null) {
            startWeatherService(mBundle);
            updateLocationAlarm(mBundle);

            Log.d(TAG, ".\nLocation Observer update...\nLocation: " + address +
                    " --> lat: " + loc.getLatitude() +
                    " - long: " + loc.getLongitude());

        // Else, we compare the lastLocation with newest for determine if we call to WeatherService
        } else {
            if (loc.distanceTo(lastLocation) > 5) { // If new location is 5 km or more
                startWeatherService(mBundle);            // far to previous one, we restart the process.
                updateLocationAlarm(mBundle);

                // Only for debug
                float distance = (float) 0.0;
                distance = lastLocation.distanceTo(loc);

                Log.d(TAG, ".\nLocation Observer update...\nLocation: " + address +
                        " --> lat: " + loc.getLatitude() +
                        " - long: " + loc.getLongitude() +
                        "\nDistance: " + distance);
            } else {
                Log.d(TAG, ".\nLocation Observer update...\nLocation: " + address +
                        " --> lat: " + loc.getLatitude() +
                        " - long: " + loc.getLongitude() +
                        "\nSame location");
            }
        }
        stopSelf();
    }

    private void startWeatherService(Bundle mBundle) {
        startService(new Intent(this, WeatherService.class).putExtras(mBundle));
    }

    private void updateLocationAlarm(Bundle mBundle) {
        if(locationAlarmIntent != null) {
            locationAlarmIntent.cancel();
        }
        locationAlarmIntent = PendingIntent.getService(
                this,
                Constants.AlarmId.LOCATION_ID,
                new Intent(this, LocationService.class).putExtras(mBundle),
                0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(am != null) {
            am.cancel(locationAlarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + Constants.Time.HOUR_MILLIS,
                    Constants.Time.HOUR_MILLIS,
                    locationAlarmIntent);
        }
        Log.i(TAG, "Next location alarm at: " + new LocalTime(System.currentTimeMillis() + Constants.Time.HOUR_MILLIS));
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mLocationClient.isConnected()) {
            Location loc = mLocationClient.getLastLocation();
            if(loc != null) {
                if (LocationHelper.isBetterLocation(loc, lastLocation)) {
                    updateLocation(loc);
                }
            }
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
