package com.androidsx.rainnotifications.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.util.SchedulerHelper;

/**
 * This receiver is responsible to init LocationService on system boot, for start all the processes.
 *
 * LocationService -> registers an alarm for call again LocationService with the previous location.
 * LocationService -> starts WeatherService.
 *
 * WeatherService -> make API call for receive the forecast.
 * WeatherService -> registers an alarm determined by the next significant forecast time.
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LocationService.class));
    }
}
