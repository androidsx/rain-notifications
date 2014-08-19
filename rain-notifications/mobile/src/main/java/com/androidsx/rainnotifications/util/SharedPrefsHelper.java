package com.androidsx.rainnotifications.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidsx.rainnotifications.Constants.SharedPref;

public class SharedPrefsHelper {

    public static final String SHARED_RAIN = "shared_rain";

    private SharedPrefsHelper() {
        //No-instantiate
    }

    public static void setNextForecast(String current, SharedPreferences.Editor editor) {
        editor.putString(SharedPref.CURRENTLY, current).commit();
    }

    public static String getNextForecast(SharedPreferences sharedPref) {
        return sharedPref.getString(SharedPref.CURRENTLY, "");
    }

    public static void setCurrentForecastIcon(int icon, SharedPreferences.Editor editor) {
        editor.putInt(SharedPref.CURRENTLY_ICON, icon).commit();
    }

    public static int getCurrentForecastIcon(SharedPreferences sharedPref) {
        return sharedPref.getInt(SharedPref.CURRENTLY_ICON, 0);
    }

    public static void setNextForecastIcon(int icon, SharedPreferences.Editor editor) {
        editor.putInt(SharedPref.NEXT_FORECAST_ICON, icon).commit();
    }

    public static int getNextForecastIcon(SharedPreferences sharedPref) {
        return sharedPref.getInt(SharedPref.NEXT_FORECAST_ICON, 0);
    }

    public static void setForecastAddress(String address, SharedPreferences.Editor editor) {
        editor.putString(SharedPref.ADDRESS, address).commit();
    }

    public static String getForecastAddress(SharedPreferences sharedPref) {
        return sharedPref.getString(SharedPref.ADDRESS, "");
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
        SharedPreferences config = context.getSharedPreferences(SharedPrefsHelper.SHARED_RAIN,
                0);
        return config.getInt(key, defaultValue);
    }
}
