package com.androidsx.rainnotifications.weatherclientfactory;

import android.content.Context;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientExecutor;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.wunderground.WundergroundNetworkServiceTask;

public abstract class WeatherClientFactory {

    private static final WeatherClient CLIENT = WeatherClient.FORECAST_IO;

    public static void requestForecastForLocation(Context context, double latitude, double longitude, WeatherClientResponseListener responseListener) {
        final WeatherClientExecutor weatherClientExecutor;
        switch (CLIENT) {
            case FORECAST_IO: weatherClientExecutor = new ForecastIoNetworkServiceTask(); break;
            case WUNDERGROUND: weatherClientExecutor = new WundergroundNetworkServiceTask(); break;
            default: throw new IllegalArgumentException("Unsupported client: " + CLIENT);
        }

        weatherClientExecutor.execute(context, latitude, longitude, responseListener);
    }
}
