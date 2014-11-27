package com.androidsx.rainnotifications.weatherclientfactory;

import android.content.Context;

import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientDailyResponseListener;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientExecutor;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientHourlyResponseListener;
import com.androidsx.rainnotifications.wunderground.WundergroundNetworkServiceTask;

public abstract class WeatherClientFactory {

    //WARNING: At the moment only WUNDERGROUND implement temperature and daily, so we can't use forecast.io See also WeatherBuilder on forecast.io module
    private static final WeatherClient CLIENT = WeatherClient.WUNDERGROUND;

    public static void requestHourlyForecastForLocation(Context context, double latitude, double longitude, WeatherClientHourlyResponseListener responseListener) {
        final WeatherClientExecutor weatherClientExecutor;
        switch (CLIENT) {
            //case FORECAST_IO: weatherClientExecutor = new ForecastIoNetworkServiceTask(); break;
            case WUNDERGROUND: weatherClientExecutor = new WundergroundNetworkServiceTask(); break;
            default: throw new IllegalArgumentException("Unsupported client: " + CLIENT);
        }

        weatherClientExecutor.executeHourly(context, latitude, longitude, responseListener);
    }

    public static void requestDailyForecastForLocation(Context context, double latitude, double longitude, WeatherClientDailyResponseListener responseListener) {

        final WeatherClientExecutor weatherClientExecutor;
        switch (CLIENT) {
            //case FORECAST_IO: weatherClientExecutor = new ForecastIoNetworkServiceTask(); break;
            case WUNDERGROUND: weatherClientExecutor = new WundergroundNetworkServiceTask(); break;
            default: throw new IllegalArgumentException("Unsupported client: " + CLIENT);
        }

        weatherClientExecutor.executeDaily(context, latitude, longitude, responseListener);
    }
}
