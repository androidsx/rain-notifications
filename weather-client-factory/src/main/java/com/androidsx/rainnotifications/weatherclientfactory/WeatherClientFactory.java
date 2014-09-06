package com.androidsx.rainnotifications.weatherclientfactory;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecastapislibrary.ForecastExecutor;
import com.androidsx.rainnotifications.forecastapislibrary.ForecastResponseListener;
import com.androidsx.rainnotifications.wunderground.WundergroundNetworkServiceTask;

public abstract class WeatherClientFactory {

    private static final WeatherClients CLIENT = WeatherClients.FORECAST_IO;

    public static ForecastExecutor getWeatherApiClient(final ForecastResponseListener forecastResponseListener) {
        if (CLIENT.equals(WeatherClients.FORECAST_IO)) {
            return new ForecastIoNetworkServiceTask(forecastResponseListener);
        } else if (CLIENT.equals(WeatherClients.WUNDERGROUND)) {
            return new WundergroundNetworkServiceTask(forecastResponseListener);
        } else {
            return null;
        }
    }

}
