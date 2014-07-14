package com.androidsx.rainnotifications.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.androidsx.rainnotifications.ForecastMobile;

public class ScheduleService extends Service {

    private static final String TAG = ScheduleService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        callForecastIO();
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    private void callForecastIO() {
        ForecastMobile.weatherObservable.getWeather(
                ForecastMobile.lastLocation.getLatitude(),
                ForecastMobile.lastLocation.getLongitude());

        Log.d(TAG, "Scheduled Forecast.io ...");
    }
}
