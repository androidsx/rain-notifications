package com.androidsx.rainnotifications.weatherclientfactory;

import android.content.Context;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.forecastapislibrary.ForecastApis;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.wunderground.WeatherClients;
import com.androidsx.rainnotifications.wunderground.WundergroundNetworkServiceTask;

public abstract class WeatherClientFactory {

    private static final WeatherClients CLIENT = WeatherClients.WUNDERGROUND;

    public static ForecastApis getWeatherApiClient(final ForecastCheckerResultListener forecastCheckerResultListener) {
        if (CLIENT.equals(WeatherClients.FORECAST_IO)) {
            return new ForecastIoNetworkServiceTask() {
                @Override
                public void execute(Context context, double latitude, double longitude) {
                    this.execute(new ForecastIoRequest(latitude, longitude).getRequest());
                }

                @Override
                public void onRequestSuccess(ForecastTable forecastTable) {
                    forecastCheckerResultListener.onForecastSuccess(forecastTable);
                }

                @Override
                public void onRequestFailure() {
                    forecastCheckerResultListener.onForecastFailure(new ForecastCheckerException());
                }
            };
        } else if (CLIENT.equals(WeatherClients.WUNDERGROUND)) {
            return new WundergroundNetworkServiceTask() {
                @Override
                public void onRequestSuccess(ForecastTable forecastTable) {
                    forecastCheckerResultListener.onForecastSuccess(forecastTable);
                }

                @Override
                public void onRequestFailure() {
                    forecastCheckerResultListener.onForecastFailure(new ForecastCheckerException());
                }
            };
        } else {
            return null;
        }
    }

    public static interface ForecastCheckerResultListener {
        public void onForecastSuccess(ForecastTable forecastTable);
        public void onForecastFailure(ForecastCheckerException exception);
    }

    public static class ForecastCheckerException extends Exception {
    }
}
