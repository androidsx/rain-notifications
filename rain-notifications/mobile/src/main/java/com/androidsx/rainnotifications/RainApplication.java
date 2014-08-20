package com.androidsx.rainnotifications;

import android.app.Application;
import android.content.SharedPreferences;

import com.androidsx.rainnotifications.utils.SharedPrefsHelper;

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
