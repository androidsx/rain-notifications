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

    public static void setNextWeatherCallAlarm(Context context, double latitude, double longitude, long time) {
        int weatherAlarmID = 0;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, LocationService.class);
        Bundle mBundle = new Bundle();
        mBundle.putDouble(Constants.Extras.EXTRA_LAT, latitude);
        mBundle.putDouble(Constants.Extras.EXTRA_LON, longitude);
        mIntent.putExtras(mBundle);
        PendingIntent alarmIntent = PendingIntent.getService(context.getApplicationContext(), weatherAlarmID, mIntent, 0);
        if(am != null) {
            am.cancel(alarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    Constants.Time.TEN_MINUTES_MILLIS,
                    alarmIntent);
        }
    }

    public static void setNextLocationAlarm(Context context, double latitude, double longitude, long time) {
        int locationAlarmID = 1;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, LocationService.class);
        Bundle mBundle = new Bundle();
        mBundle.putDouble(Constants.Extras.EXTRA_LAT, latitude);
        mBundle.putDouble(Constants.Extras.EXTRA_LON, longitude);
        mIntent.putExtras(mBundle);
        PendingIntent alarmIntent = PendingIntent.getService(context.getApplicationContext(), locationAlarmID, mIntent, 0);
        if(am != null) {
            am.cancel(alarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + time,
                    time,
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
