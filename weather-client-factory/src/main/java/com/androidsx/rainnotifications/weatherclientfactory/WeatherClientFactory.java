package com.androidsx.rainnotifications.weatherclientfactory;

import android.content.Context;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientExecutor;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.wunderground.WundergroundNetworkServiceTask;

public abstract class WeatherClientFactory {

    private static final WeatherClient CLIENT = WeatherClient.FORECAST_IO;

    public static void requestForecastForLocation(Context context, double latitude, double longitude, WeatherClientResponseListener responseListener) {
        if (rightCoordinates(latitude, longitude)) {
            final WeatherClientExecutor weatherClientExecutor;
            if (CLIENT.equals(WeatherClient.FORECAST_IO)) {
                weatherClientExecutor = new ForecastIoNetworkServiceTask();
            } else if (CLIENT.equals(WeatherClient.WUNDERGROUND)) {
                weatherClientExecutor = new WundergroundNetworkServiceTask();
            } else {
                throw new IllegalArgumentException("Unsupported client: " + CLIENT);
            }

            weatherClientExecutor.execute(context, latitude, longitude, responseListener);
        }
        // TODO: what if the coordinates are not right?
    }

    // TODO: is this useful at all?
    private static boolean rightCoordinates(double latitude, double longitude) {
        if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
            return true;
        } else {
            return false;
        }
    }
}
