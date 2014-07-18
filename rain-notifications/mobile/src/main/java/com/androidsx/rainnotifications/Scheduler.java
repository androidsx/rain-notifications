package com.androidsx.rainnotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;

import com.androidsx.rainnotifications.util.Constants;

import com.forecast.io.v2.transfer.DataPoint;

public class Scheduler {

    private static final String TAG = Scheduler.class.getSimpleName();

    private static final long HOUR = Constants.Time.HOUR_AGO / 1000;
    private static final long TEN_MINUTES = Constants.Time.TEN_MINUTES_AGO / 1000;

    public void setNextApiCallAlarm(AlarmManager am, PendingIntent pi, DataPoint dp) {
        am.cancel(pi);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                nextApiCallTime(dp) * 1000,
                nextApiCallTime(dp) * 1000,
                pi);
    }

    public void setNextLocationAlarm(AlarmManager am, PendingIntent pi, long time) {
        am.cancel(pi);
        am.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + time * 1000,
                time * 1000,
                pi);
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
}
