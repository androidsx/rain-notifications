package com.androidsx.rainnotifications.backgroundservice.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsHelper {

    public static final String SHARED_RAIN = "shared_rain";

    private SharedPrefsHelper() {
        // Non-instantiable
    }

    public static void saveLongValue(Context context, String key, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SharedPrefsHelper.SHARED_RAIN, 0).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLongValue(Context context, String key) {
        return SharedPrefsHelper.getLongValueInternal(context, key, 0);
    }

    public static void saveIntValue(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SharedPrefsHelper.SHARED_RAIN, 0).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getIntValue(Context context, String key) {
        return SharedPrefsHelper.getIntValueInternal(context, key, 0);
    }

    private static int getIntValueInternal(Context context, String key, int defaultValue) {
        SharedPreferences config = context.getSharedPreferences(SharedPrefsHelper.SHARED_RAIN, 0);
        return config.getInt(key, defaultValue);
    }

    private static long getLongValueInternal(Context context, String key, long defaultValue) {
        SharedPreferences config = context.getSharedPreferences(SharedPrefsHelper.SHARED_RAIN, 0);
        return config.getLong(key, defaultValue);
    }
}
