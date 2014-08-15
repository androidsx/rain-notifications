package com.androidsx.rainnotifications.util;

import android.content.SharedPreferences;
import com.androidsx.rainnotifications.Constants.SharedPref;

public class SharedPrefsHelper {

    private SharedPrefsHelper() {
        //No-instantiate
    }

    public static void setFirstTimeExecution(boolean bool, SharedPreferences.Editor editor) {
        editor.putBoolean(SharedPref.FIRST_TIME_EXECUTION, bool).commit();
    }

    public static boolean getFirstTimeExecution(SharedPreferences sharedPref) {
        return sharedPref.getBoolean(SharedPref.FIRST_TIME_EXECUTION, false);
    }

    public static void setCurrentForecast(String current, SharedPreferences.Editor editor) {
        editor.putString(SharedPref.CURRENTLY, current).commit();
    }

    public static String getCurrentForecast(SharedPreferences sharedPref) {
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

    public static void setLogHistory(String history, SharedPreferences.Editor editor) {
        editor.putString(SharedPref.HISTORY, history).commit();
    }

    public static String getLogHistory(SharedPreferences sharedPref) {
        return sharedPref.getString(SharedPref.HISTORY, "");
    }
}
