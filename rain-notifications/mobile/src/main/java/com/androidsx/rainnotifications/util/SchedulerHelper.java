package com.androidsx.rainnotifications.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.androidsx.rainnotifications.Constants;

/*
 * This helper class is for register new Alarms with extras, for call our services at assigned time.
 */

public class SchedulerHelper {

    private SchedulerHelper() {
        // Non-instantiable
    }

    public static void setAlarm(Context context, int id, Class<? extends Service> service, double latitude, double longitude, long initTime, long repeatTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, service);
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

    // That method is for determine the next time that we must call again to WeatherService.
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
