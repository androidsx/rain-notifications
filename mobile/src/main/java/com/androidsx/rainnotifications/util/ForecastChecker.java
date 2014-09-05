package com.androidsx.rainnotifications.util;

import android.content.Context;

import com.androidsx.rainnotifications.weatherclientfactory.WeatherClientFactory;

/**
 * Class that calls for obtain the weather forecast.
 */
public abstract class ForecastChecker implements WeatherClientFactory.ForecastCheckerResultListener {

    /**
     * Asynchronous method that call for obtain the weather forecast into a determined location.
     */
    public void requestForecastForLocation(Context context, double latitude, double longitude) {
        if (LocationHelper.rightCoordinates(latitude, longitude)) {
            WeatherClientFactory.getWeatherApiClient(this).execute(context, latitude, longitude);
        }
    }
}
