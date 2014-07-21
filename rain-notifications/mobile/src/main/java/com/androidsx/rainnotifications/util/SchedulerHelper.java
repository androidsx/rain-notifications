package com.androidsx.rainnotifications.util;

import android.app.AlarmManager;
import android.app.PendingIntent;

import com.androidsx.rainnotifications.Constants;

/*
 * Clase auxiliar, para registrar las alarmas de los servicios WeatherService y LocationService.
 */

public class SchedulerHelper {

    private SchedulerHelper() {
        // Non-instantiable
    }

    public static void setNextWeatherCallAlarm(AlarmManager am, PendingIntent pi, long time) {
        am.cancel(pi);
        am.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                time,
                Constants.Time.TEN_MINUTES_MILLIS,
                pi);
    }

    public static void setNextLocationAlarm(AlarmManager am, PendingIntent pi, long time) {
        am.cancel(pi);
        am.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + time,
                time,
                pi);
    }

    public static long nextApiCallTime(long time) {
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
