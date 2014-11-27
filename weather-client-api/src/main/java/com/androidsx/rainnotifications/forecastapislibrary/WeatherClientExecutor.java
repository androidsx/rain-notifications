package com.androidsx.rainnotifications.forecastapislibrary;

import android.content.Context;

/**
 * Asynchronous executor for requests to a weather data provider.
 */
public interface WeatherClientExecutor {

    /**
     * Executes an hourly request in a background thread.
     *
     * @param responseListener where the results will be returned after the call is performed
     */
    public void executeHourly(Context context, double latitude, double longitude, WeatherClientHourlyResponseListener responseListener);

    /**
     * Executes a daily request in a background thread.
     *
     * @param responseListener where the results will be returned after the call is performed
     */
    public void executeDaily(Context context, double latitude, double longitude, WeatherClientDailyResponseListener responseListener);
}
