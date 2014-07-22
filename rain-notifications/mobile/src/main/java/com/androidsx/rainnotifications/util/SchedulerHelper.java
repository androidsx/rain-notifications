package com.androidsx.rainnotifications.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.service.LocationService;

/*
 * Clase auxiliar, para registrar las alarmas de los servicios WeatherService y LocationService.
 */

public class SchedulerHelper {

    private SchedulerHelper() {
        // Non-instantiable
    }

    public static void setAlarm(Context context, int id, double latitude, double longitude, long initTime, long repeatTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, context.getClass());
        Bundle mBundle = new Bundle();
        mBundle.putDouble(Constants.Extras.EXTRA_LAT, latitude);
        mBundle.putDouble(Constants.Extras.EXTRA_LON, longitude);
        mIntent.putExtras(mBundle);
        PendingIntent alarmIntent = PendingIntent.getService(context.getApplicationContext(), id, mIntent, 0);
        if(am != null) {
            am.cancel(alarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    initTime,
                    repeatTime,
                    alarmIntent);
        }
    }

    public static long nextWeatherCallAlarm(long time) {
        final long currentTime = System.currentTimeMillis();
        if (time != 0) {
            if ((time - currentTime) < Constants.Time.TEN_MINUTES_MILLIS) {
                return time;
            } else if (time - currentTime < (2 * Constants.Time.HOUR_MILLIS)){
                return currentTime + ((time - currentTime) * 70 / 100);
            } else {
                return currentTime + (2 * Constants.Time.HOUR_MILLIS);
            }
        } else {
            return currentTime + (2 * Constants.Time.HOUR_MILLIS);
        }
    }
}
