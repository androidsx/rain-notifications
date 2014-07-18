package com.androidsx.rainnotifications.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class WeatherService extends Service {

    private static final String TAG = WeatherService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle mBundle = intent.getExtras();
            if(mBundle != null) {
                double latitude = mBundle.getDouble(ForecastService.EXTRA_LAT, 1000);
                double longitude = mBundle.getDouble(ForecastService.EXTRA_LON, 1000);

                if (latitude != 1000 && longitude != 1000) {
                    ForecastService.weatherObservable.checkForecast(latitude, longitude);
                }
            }
        }
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }
}
