package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.util.LocationHelper;
import com.androidsx.rainnotifications.Constants;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.joda.time.DateTimeConstants;
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

    private static final float DEFAULT_DISTANCE = 5000.0f;
    private static final double BAD_COORDINATE = 200; //For indicate that there is no coordinates into extras.
    private static final long LOCATION_EXTRA_TIME_MILLIS = 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long LOCATION_REPEATING_TIME_MILLIS = 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long SPECIAL_LOCATION_EXTRA_TIME_MILLIS = 1 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long SPECIAL_LOCATION_REPEATING_TIME_MILLIS = 5 * DateTimeConstants.MILLIS_PER_MINUTE;

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

                double latitude = mBundle.getDouble(Constants.Extras.EXTRA_LAT, BAD_COORDINATE);
                double longitude = mBundle.getDouble(Constants.Extras.EXTRA_LON, BAD_COORDINATE);

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
        String address = getLocationAddress(this,
                loc.getLatitude(), loc.getLongitude());

        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.Extras.EXTRA_ADDRESS, address);
        mBundle.putDouble(Constants.Extras.EXTRA_LAT, loc.getLatitude());
        mBundle.putDouble(Constants.Extras.EXTRA_LON, loc.getLongitude());

        // If LocationService is called without extras, we call WeatherService with the location
        // and registers an alarm for be called again later with this location into extras.
        if(lastLocation == null) {
            startWeatherService(mBundle);
            updateLocationAlarm(mBundle, LOCATION_EXTRA_TIME_MILLIS, LOCATION_REPEATING_TIME_MILLIS);

            Log.d(TAG, ".\nLocation Observer update...\nLocation: " + address +
                    " --> lat: " + loc.getLatitude() +
                    " - long: " + loc.getLongitude());

        // Else, we compare the lastLocation with newest for determine if we call to WeatherService
        } else {
            if (loc.distanceTo(lastLocation) > DEFAULT_DISTANCE) { // If new location is 5 km or more
                startWeatherService(mBundle);            // far to previous one, we restart the process.
                updateLocationAlarm(mBundle, LOCATION_EXTRA_TIME_MILLIS, LOCATION_REPEATING_TIME_MILLIS);

                Log.d(TAG, ".\nLocation Observer update...\nLocation: " + address +
                        " --> lat: " + loc.getLatitude() +
                        " - long: " + loc.getLongitude() +
                        "\nDistance: " + lastLocation.distanceTo(loc));
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

    private void updateLocationAlarm(Bundle mBundle, long extraTime, long repeatingTime) {
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
                    System.currentTimeMillis() + extraTime,
                    repeatingTime,
                    locationAlarmIntent);
        }
        Log.i(TAG, "Next location alarm at: " + new LocalTime(System.currentTimeMillis() + LOCATION_EXTRA_TIME_MILLIS));
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mLocationClient.isConnected()) {
            Location loc = mLocationClient.getLastLocation();
            if(loc != null) {
                if (LocationHelper.isBetterLocation(loc, lastLocation)) {
                    updateLocation(loc);
                }
            } else {
                // TODO: probably notify to user, that the gps is disabled or not available,
                // if we try to obtain many times the location.
                Bundle b = new Bundle();
                if(lastLocation != null) {
                    b.putDouble(Constants.Extras.EXTRA_LAT, lastLocation.getLatitude());
                    b.putDouble(Constants.Extras.EXTRA_LON, lastLocation.getLongitude());
                } else {
                    b.putDouble(Constants.Extras.EXTRA_LAT, BAD_COORDINATE); // Bad location coordinates
                    b.putDouble(Constants.Extras.EXTRA_LON, BAD_COORDINATE); // for simulate first call.
                }
                updateLocationAlarm(b, SPECIAL_LOCATION_EXTRA_TIME_MILLIS, SPECIAL_LOCATION_REPEATING_TIME_MILLIS);
            }
        }
    }

    public static String getLocationAddress(Context context, double latitude, double longitude) {
        String address = context.getString(R.string.current_name_location);

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            if(addresses.get(0).getAddressLine(0) != null) address = addresses.get(0).getAddressLine(0);
            else if(addresses.get(0).getSubLocality() != null) address = addresses.get(0).getSubLocality();
            else if(addresses.get(0).getLocality() != null) address = addresses.get(0).getLocality();
            else if(addresses.get(0).getCountryName() != null) address = addresses.get(0).getCountryName();
        }

        return address;
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
