package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.androidsx.rainnotifications.model.WeatherObservable;
import com.androidsx.rainnotifications.util.Constants;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;
import com.forecast.io.v2.transfer.DataPoint;

public class ScheduleService extends Service {

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
            startService(new Intent(this, WeatherService.class));
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    public void setNextApiCallAlarm(AlarmManager am, PendingIntent pi, DataPoint dp) {
        am.cancel(pi);
        am.set(AlarmManager.RTC_WAKEUP, nextApiCallTime(dp) * 1000, pi
        );
        WeatherService.sharedHelper.setNextAlarmTime(nextApiCallTime(dp));
    }

    public void setNextApiCallAlarm(AlarmManager am, PendingIntent pi, long time) {
        am.cancel(pi);
        am.set(AlarmManager.RTC_WAKEUP, time * 1000, pi);
    }

    public long nextApiCallTime(DataPoint dp) {
        long currentTime = System.currentTimeMillis() / 1000;
        if(dp != null) {
            if((dp.getTime() - currentTime) < TEN_MINUTES) {
                return dp.getTime();
            } else {
                return currentTime + ((dp.getTime() - currentTime) * 70 / 100);
            }
        } else {
            return currentTime + (4 * HOUR);
        }
    }
}
