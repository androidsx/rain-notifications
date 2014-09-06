package com.androidsx.rainnotifications.forecastapislibrary;

import android.content.Context;

/**
 * Asynchronous executor for requests to a weather data provider.
 */
public interface WeatherClientExecutor {

    /**
     * Executes the request in a background thread.
     *
     * @param responseListener where the results will be returned after the call is performed
     */
    public void execute(Context context,
                        double latitude,
                        double longitude,
                        WeatherClientResponseListener responseListener);
}
