package com.androidsx.rainnotifications.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidsx.rainnotifications.Constants;

public class DebugHelper {

    private static final String TAG = DebugHelper.class.getSimpleName();

    private DebugHelper() {
        //No-instantiate
    }

    public static void displayDebugResults(Context context, SharedPreferences sharedPref, long nextIconTime, String nextForecastIcon, String currentlyIcon, String searchingIcon) {
        String history = SharedPrefsHelper.getForecastHistory(sharedPref);

        String update = "";
        String currentTime = DateHelper
                .formatTimeMadrid(System.currentTimeMillis());
        String nextApiCall = DateHelper
                .formatTimeMadrid(SchedulerHelper.nextApiCallTime(nextIconTime));
        if(nextIconTime == 0) {
            update = "\nSearching: " + searchingIcon + "\n\nCurrently: " + currentlyIcon +
                    " at "+ currentTime +
                    "\n\nNo changes expected until tomorrow." +
                    "\n\nNext API call at: " + nextApiCall;
        }
        else {
            String deltaTime = DateHelper
                    .deltaTime(nextIconTime, System.currentTimeMillis());

            String forecastTime = DateHelper
                    .formatTimeMadrid(nextIconTime);

            if(AnalyzerHelper.compareIconToIcon(nextForecastIcon, searchingIcon)) {
                update = "\nFound: " + nextForecastIcon + "\n\nCurrently: " + currentlyIcon +
                        "\nat "+ currentTime +
                        "\n\n" + nextForecastIcon + " expected at " + forecastTime +
                        " \n" + deltaTime + ".\n\nNext API call at: " + nextApiCall;
            } else {
                update = "\nSearching: " + searchingIcon + "\n\nCurrently: " + currentlyIcon +
                        "\nat "+ currentTime +
                        "\n\n" + nextForecastIcon + " expected at " + forecastTime +
                        " \n" + deltaTime + ".\n\nNext API call at: " + nextApiCall;
            }
        }
        history += update + "\n--------------------";
        if(nextIconTime == 0) {
            update = "No changes expected until tomorrow." +
                    "\n\nNext API call at: " + nextApiCall + "\n";
            SharedPrefsHelper.setCurrentForecastIcon(currentlyIcon, sharedPref.edit());
            SharedPrefsHelper.setNextForecastIcon(currentlyIcon, sharedPref.edit());
        }
        else {
            String deltaTime = DateHelper
                    .deltaTime(nextIconTime, System.currentTimeMillis());
            if(AnalyzerHelper.compareIconToIcon(nextForecastIcon, searchingIcon)) {
                update = deltaTime + ".\n\nNext API call at: " + nextApiCall + "\n";
            } else {
                update = deltaTime + ".\n\nNext API call at: " + nextApiCall + "\n";
            }
            SharedPrefsHelper.setCurrentForecastIcon(currentlyIcon, sharedPref.edit());
            SharedPrefsHelper.setNextForecastIcon(nextForecastIcon, sharedPref.edit());
        }
        Log.d(TAG, ".\n" + update);

        SharedPrefsHelper.setCurrentForecast(update, sharedPref.edit());
        SharedPrefsHelper.setForecastHistory(history, sharedPref.edit());

        String deltaTime = DateHelper
                .deltaTime(nextIconTime, System.currentTimeMillis());
        String expectedTime = DateHelper
                .formatTimeMadrid(nextIconTime);
        if(!currentlyIcon.equals(Constants.ForecastIO.Icon.RAIN) && nextForecastIcon.equals(Constants.ForecastIO.Icon.RAIN)) {
            NotificationHelper.sendNotification(context, 0,
                    Constants.ForecastIO.FORECAST_ICON.get(nextForecastIcon),
                    "Rain expected " + deltaTime + " at " + expectedTime);
        } else if(currentlyIcon.equals(Constants.ForecastIO.Icon.RAIN) && !nextForecastIcon.equals(Constants.ForecastIO.Icon.RAIN)) {
            NotificationHelper.sendNotification(context, 0,
                    Constants.ForecastIO.FORECAST_ICON.get(nextForecastIcon),
                    "Stop raining expected " + deltaTime + " at " + expectedTime);
        }
    }
}
