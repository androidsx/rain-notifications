package com.androidsx.rainnotifications.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.androidsx.rainnotifications.util.Constants.SharedPref;

public class SharedPrefsHelper {

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    public SharedPrefsHelper(Context context) {
        shared = context.getSharedPreferences(SharedPref.SHARED_RAIN, 0);
        editor = shared.edit();
    }

    public void setNextAlarmTime(long time) {
        editor.putLong(SharedPref.NEXT_ALARM_TIME, time).commit();
    }

    public long getNextAlarmTime() {
        return shared.getLong(SharedPref.NEXT_ALARM_TIME, 0);
    }

    public void setNextForecast(String current) {
        editor.putString(SharedPref.CURRENTLY, current).commit();
    }

    public String getNextForecast() {
        return shared.getString(SharedPref.CURRENTLY, "");
    }

    public void setCurrentForecastIcon(String icon) {
        editor.putString(SharedPref.CURRENTLY_ICON, icon).commit();
    }

    public String getCurrentForecastIcon() {
        return shared.getString(SharedPref.CURRENTLY_ICON, "");
    }

    public void setNextForecastIcon(String icon) {
        editor.putString(SharedPref.NEXT_FORECAST_ICON, icon).commit();
    }

    public String getNextForecastIcon() {
        return shared.getString(SharedPref.NEXT_FORECAST_ICON, "");
    }

    public void setForecastAddress(String address) {
        editor.putString(SharedPref.ADDRESS, address).commit();
    }

    public String getForecastAddress() {
        return shared.getString(SharedPref.ADDRESS, "");
    }

    public void setForecastHistory(String history) {
        editor.putString(SharedPref.HISTORY, history).commit();
    }

    public String getForecastHistory() {
        return shared.getString(SharedPref.HISTORY, "");
    }
}
