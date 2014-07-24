package com.androidsx.rainnotifications.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.service.WeatherService;

import org.joda.time.LocalTime;

/*
 * This helper class is for register new Alarms with extras, for call our services at assigned time.
 */

public class SchedulerHelper {

    //private static PendingIntent weatherAlarmIntent;
    //private static PendingIntent locationAlarmIntent;

    private SchedulerHelper() {
        // Non-instantiable
    }

    public static void setAlarm(Context context, PendingIntent pi, long initTime, long repeatTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am != null) {
            am.cancel(pi);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    initTime,
                    repeatTime,
                    pi);
        }
    }
}
