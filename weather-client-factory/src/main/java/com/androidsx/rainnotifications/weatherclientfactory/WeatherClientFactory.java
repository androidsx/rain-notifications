package com.androidsx.rainnotifications.weatherclientfactory;

import android.content.Context;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecastapislibrary.ForecastExecutor;
import com.androidsx.rainnotifications.forecastapislibrary.ForecastResponseListener;
import com.androidsx.rainnotifications.wunderground.WundergroundNetworkServiceTask;

public abstract class WeatherClientFactory {

    private static final WeatherClients CLIENT = WeatherClients.FORECAST_IO;

    public static void requestForecastForLocation(Context context, double latitude, double longitude, ForecastResponseListener responseListener) {
        if (rightCoordinates(latitude, longitude)) {
            final ForecastExecutor forecastExecutor;
            if (CLIENT.equals(WeatherClients.FORECAST_IO)) {
                forecastExecutor = new ForecastIoNetworkServiceTask();
            } else if (CLIENT.equals(WeatherClients.WUNDERGROUND)) {
                forecastExecutor = new WundergroundNetworkServiceTask();
            } else {
                throw new IllegalArgumentException("Unsupported client: " + CLIENT);
            }

            forecastExecutor.execute(context, latitude, longitude, responseListener);
        }
    }

    private static boolean rightCoordinates(double latitude, double longitude) {
        if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
            return true;
        } else {
            return false;
        }
    }
}
