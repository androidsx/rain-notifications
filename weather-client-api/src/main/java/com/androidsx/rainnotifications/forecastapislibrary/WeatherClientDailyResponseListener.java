package com.androidsx.rainnotifications.forecastapislibrary;

import com.androidsx.rainnotifications.model.DailyForecastTable;

/**
 * Listener for a response from a weather client.
 */
public interface WeatherClientDailyResponseListener {

    /**
     * Handles the case when the request for weather information succeeded. Note that this method
     * is executed in the UI thread.
     */
    public void onForecastSuccess(DailyForecastTable dailyForecastTable);

    /**
     * Handles the case when the request for weather information failed.
     */
    public void onForecastFailure(WeatherClientException weatherClientException);


}
