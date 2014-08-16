package com.androidsx.rainnotifications;

import android.app.Application;
import android.content.SharedPreferences;

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
    }

    private void setupLogging() {
        Timber.plant(
                new AlphaLogReporting(
                        getSharedPreferences(Constants.SharedPref.SHARED_RAIN, 0)
                )
        );
    }

    private static class AlphaLogReporting extends Timber.DebugTree {

        private SharedPreferences sharedPrefs;
        private String log;

        public AlphaLogReporting(SharedPreferences sharedPrefs) {
            this.sharedPrefs = sharedPrefs;
            this.log = SharedPrefsHelper.getLogHistory(sharedPrefs);
        }

        @Override
        public void i(String message, Object... args) {
            super.i(message, args);
            log += String.format("\n" + message, args);
            SharedPrefsHelper.setLogHistory(log, sharedPrefs.edit());
        }
    }
}
