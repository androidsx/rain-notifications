package com.androidsx.rainnotifications.util;

import android.content.Context;
import android.util.Log;

public class DebugHelper {

    private static final String TAG = DebugHelper.class.getSimpleName();

    private DebugHelper() {
        //No-instantiate
    }

    public static void displayDebugResults(Context context, SharedPrefsHelper shared, long nextIconTime, String nextForecastIcon, String currentlyIcon, String searchingIcon) {
        String history = shared.getForecastHistory();

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
            shared.setCurrentForecastIcon(currentlyIcon);
            shared.setNextForecastIcon(currentlyIcon);
        }
        else {
            String deltaTime = DateHelper
                    .deltaTime(nextIconTime, System.currentTimeMillis());
            if(AnalyzerHelper.compareIconToIcon(nextForecastIcon, searchingIcon)) {
                update = deltaTime + ".\n\nNext API call at: " + nextApiCall + "\n";
            } else {
                update = deltaTime + ".\n\nNext API call at: " + nextApiCall + "\n";
            }
            shared.setCurrentForecastIcon(currentlyIcon);
            shared.setNextForecastIcon(nextForecastIcon);
        }
        Log.d(TAG, ".\n" + update);

        shared.setCurrentForecast(update);
        shared.setForecastHistory(history);

        String deltaTime = DateHelper
                .deltaTime(nextIconTime, System.currentTimeMillis());
        String expectedTime = DateHelper
                .formatTimeMadrid(nextIconTime);
        if(!currentlyIcon.equals(Constants.ForecastIO.Icon.RAIN) && nextForecastIcon.equals(Constants.ForecastIO.Icon.RAIN)) {
            NotificationHelper.sendNotification(context, 0,
                    WeatherIconHelper.getWeatherIcon(nextForecastIcon),
                    "Rain expected " + deltaTime + " at " + expectedTime);
        } else if(currentlyIcon.equals(Constants.ForecastIO.Icon.RAIN) && !nextForecastIcon.equals(Constants.ForecastIO.Icon.RAIN)) {
            NotificationHelper.sendNotification(context, 0,
                    WeatherIconHelper.getWeatherIcon(nextForecastIcon),
                    "Stop raining expected " + deltaTime + " at " + expectedTime);
        }
    }
}
