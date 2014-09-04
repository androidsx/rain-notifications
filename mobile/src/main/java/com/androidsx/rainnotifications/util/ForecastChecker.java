package com.androidsx.rainnotifications.util;

import android.content.Context;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.wunderground.WundergroundNetworkServiceTask;

/**
 * Class that calls for obtain the weather forecast.
 */
public class ForecastChecker {

    public static final String FORECAST_IO = "forecast-io";
    public static final String WUNDERGROUND = "wunderground";

    /**
     * Asynchronous method that call for obtain the weather forecast into a determined location.
     */
    public static void requestForecastForLocation(Context context, String provider, double latitude, double longitude,
                                                  final ForecastCheckerResultListener forecastCheckerResultListener) {
        if (LocationHelper.rightCoordinates(latitude, longitude)) {
            if (provider.equals(FORECAST_IO)) {
                new ForecastIoNetworkServiceTask() {
                    @Override
                    public void onRequestSuccess(ForecastTable forecastTable) {
                        forecastCheckerResultListener.onForecastSuccess(forecastTable);
                    }
                    @Override
                    public void onRequestFailure() {
                        forecastCheckerResultListener.onForecastFailure(new ForecastCheckerException());
                    }
                }.execute(new ForecastIoRequest(latitude, longitude).getRequest());

            } else if (provider.equals(WUNDERGROUND)) {
                new WundergroundNetworkServiceTask() {
                    @Override
                    public void onRequestSuccess(ForecastTable forecastTable) {
                        forecastCheckerResultListener.onForecastSuccess(forecastTable);
                    }

                    @Override
                    public void onRequestFailure() {
                        forecastCheckerResultListener.onForecastFailure(new ForecastCheckerException());
                    }
                }.execute(context, latitude, longitude);
            }
        }
    }

    public static interface ForecastCheckerResultListener {
        public void onForecastSuccess(ForecastTable forecastTable);
        public void onForecastFailure(ForecastCheckerException exception);
    }

    public static class ForecastCheckerException extends Exception {
    }
}
