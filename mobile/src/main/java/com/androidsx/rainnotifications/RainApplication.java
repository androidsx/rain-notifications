package com.androidsx.rainnotifications;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.util.AlarmHelper;
import com.androidsx.rainnotifications.util.ApplicationVersionHelper;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;

import timber.log.Timber;

public class RainApplication extends Application {
    private static RainApplication instance;

    public RainApplication() {
        instance = this;
    }

    public static RainApplication getApplication() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setupLogging();
        trackAppUsage();
        startWeatherServiceIfNecessary();
        startDayAlarmIfNecessary();
    }

    /**
     * Returns the history of INFO logs recorded so far in Logcat. This output is obviously only for
     * debugging purposes.
     */
    public String getLogHistory() {
        return AlphaLogReporting.getLogHistory(getSharedPreferences(SharedPrefsHelper.SHARED_RAIN, 0));
    }

    private void setupLogging() {
        // TODO: Create the concept of environments, and have a different tree for Live
        Timber.plant(
                new AlphaLogReporting(
                        getSharedPreferences(SharedPrefsHelper.SHARED_RAIN, 0)
                )
        );
    }

    /**
     * Tracks this usage of the application.
     */
    private void trackAppUsage() {
        final int numUsages = ApplicationVersionHelper.getNumUses(this);
        if (numUsages == 0) {
            Timber.i("New install. Setting the usage count to 0");
        } else {
            Timber.d("Usage number #" + (numUsages + 1));
        }

        ApplicationVersionHelper.saveNewUse(this);
        ApplicationVersionHelper.saveCurrentVersionCode(this);
    }

    private void startWeatherServiceIfNecessary() {
        final PendingIntent ongoingAlarm = PendingIntent.getService(this,
                Constants.Alarms.WEATHER_ID,
                new Intent(getApplicationContext(), WeatherService.class),
                PendingIntent.FLAG_NO_CREATE);
        if (ongoingAlarm == null) {
            Timber.i("The alarm is not set. Let's start the weather service now");
            startService(new Intent(this, WeatherService.class));
        } else {
            Timber.d("The alarm is already set, so we won't start the weather service");
        }
    }

    private void startDayAlarmIfNecessary() {
        final PendingIntent ongoingDayAlarm = PendingIntent.getService(this,
                Constants.Alarms.DAY_ALARM_ID,
                new Intent(getApplicationContext(), WeatherService.class),
                PendingIntent.FLAG_NO_CREATE);
        if (ongoingDayAlarm == null) {
            Timber.i("The day alarm is not set. Let's start the day alarm now");
            final PendingIntent dayAlarmIntent = PendingIntent.getService(
                    this,
                    Constants.Alarms.DAY_ALARM_ID,
                    new Intent(this, WeatherService.class).putExtra(Constants.Extras.EXTRA_DAY_ALARM, Constants.Alarms.DAY_ALARM_ID),
                    0);
            AlarmHelper.setDayAlarm(this, Constants.Alarms.HOUR_OF_THE_DAY_DIGEST_ALARM, dayAlarmIntent);
        } else {
            Timber.d("The day alarm is already set, so we won't start the day alarm");
        }
    }

    /**
     * Timber tree for alpha releases, that keeps track of everything logged into the INFO level. Do
     * NOT use in a Live environment, the internal shared preference grows out of control, and it
     * does I/O operations in the UI thread.
     */
    private static class AlphaLogReporting extends Timber.DebugTree {
        private static final String HISTORY_SHARED_PREF = "history";

        private final SharedPreferences sharedPrefs;
        private final StringBuilder currentLog;

        public AlphaLogReporting(SharedPreferences sharedPrefs) {
            this.sharedPrefs = sharedPrefs;
            currentLog = new StringBuilder(getLogHistory(sharedPrefs));
        }

        @Override
        public void i(String message, Object... args) {
            super.i(message, args);
            currentLog.append(String.format("\n" + message, args));
            sharedPrefs.edit().putString(HISTORY_SHARED_PREF, currentLog.toString()).commit();
        }

        private static String getLogHistory(SharedPreferences sharedPref) {
            return sharedPref.getString(HISTORY_SHARED_PREF, "");
        }
    }
}
