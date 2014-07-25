package com.androidsx.rainnotifications.util;

import android.content.SharedPreferences;
import com.androidsx.rainnotifications.Constants.SharedPref;

public class SharedPrefsHelper {

    private SharedPrefsHelper() {
        //No-instantiate
    }

    public static void setCurrentForecast(String current, SharedPreferences.Editor editor) {
        editor.putString(SharedPref.CURRENTLY, current).commit();
    }

    public static String getCurrentForecast(SharedPreferences sharedPref) {
        return sharedPref.getString(SharedPref.CURRENTLY, "");
    }

    public static void setCurrentForecastIcon(String icon, SharedPreferences.Editor editor) {
        editor.putString(SharedPref.CURRENTLY_ICON, icon).commit();
    }

    public static String getCurrentForecastIcon(SharedPreferences sharedPref) {
        return sharedPref.getString(SharedPref.CURRENTLY_ICON, "");
    }

    public static void setNextForecastIcon(String icon, SharedPreferences.Editor editor) {
        editor.putString(SharedPref.NEXT_FORECAST_ICON, icon).commit();
    }

    public static String getNextForecastIcon(SharedPreferences sharedPref) {
        return sharedPref.getString(SharedPref.NEXT_FORECAST_ICON, "");
    }

    public static void setForecastAddress(String address, SharedPreferences.Editor editor) {
        editor.putString(SharedPref.ADDRESS, address).commit();
    }

    public static String getForecastAddress(SharedPreferences sharedPref) {
        return sharedPref.getString(SharedPref.ADDRESS, "");
    }

    public static void setForecastHistory(String history, SharedPreferences.Editor editor) {
        editor.putString(SharedPref.HISTORY, history).commit();
    }

    public static String getForecastHistory(SharedPreferences sharedPref) {
        return sharedPref.getString(SharedPref.HISTORY, "");
    }
}
