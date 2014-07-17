package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

import com.androidsx.rainnotifications.util.Constants;

import com.forecast.io.v2.transfer.DataPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class ScheduleService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = ScheduleService.class.getSimpleName();

    private static final long HOUR = Constants.Time.HOUR_AGO / 1000;
    private static final long TEN_MINUTES = Constants.Time.TEN_MINUTES_AGO / 1000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle mBundle = intent.getExtras();
            if(mBundle == null) {
                startService(new Intent(this, WeatherService.class));

            } else {
                double lastLatitude = mBundle.getDouble(WeatherService.EXTRA_LAT);
                double lastLongitude = mBundle.getDouble(WeatherService.EXTRA_LON);

                Location lastLocation = new Location(LocationManager.NETWORK_PROVIDER);
                lastLocation.setLatitude(lastLatitude);
                lastLocation.setLongitude(lastLongitude);

                LocationClient mLocationClient = new LocationClient(this, this, this);
                Location mLocation = mLocationClient.getLastLocation();

                if(mLocation.distanceTo(lastLocation) > Constants.Distance.KM * 5) {
                    startService(new Intent(this, WeatherService.class));
                }
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    public void setNextApiCallAlarm(AlarmManager am, PendingIntent pi, DataPoint dp) {
        am.cancel(pi);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() +
                    (nextApiCallTime(dp) * 1000 - System.currentTimeMillis()), pi
        );
        WeatherService.sharedHelper.setNextAlarmTime(nextApiCallTime(dp));
    }

    public void setNextApiCallAlarm(AlarmManager am, PendingIntent pi, long time) {
        am.cancel(pi);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        (time * 1000 - System.currentTimeMillis()), pi);
    }

    public void setNextLocationAlarm(AlarmManager am, PendingIntent pi, long time) {
        am.cancel(pi);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        (time * 1000 - System.currentTimeMillis()), pi);
    }

    public long nextApiCallTime(DataPoint dp) {
        long currentTime = System.currentTimeMillis() / 1000;
        if(dp != null) {
            if((dp.getTime() - currentTime) < TEN_MINUTES) {
                return dp.getTime();
            } else if(dp.getTime() - currentTime < (2 * HOUR)){
                return currentTime + ((dp.getTime() - currentTime) * 70 / 100);
            } else {
                return currentTime + (2 * HOUR);
            }
        } else {
            return currentTime + (2 * HOUR);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
