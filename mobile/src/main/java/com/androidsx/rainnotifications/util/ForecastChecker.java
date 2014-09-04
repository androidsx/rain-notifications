package com.androidsx.rainnotifications.util;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.ForecastTable;

/**
 * Class that calls for obtain the weather forecast.
 */
public class ForecastChecker {

    /**
     * Asynchronous method that call for obtain the weather forecast into a determined location.
     */
    public static void requestForecastForLocation(double latitude, double longitude,
                                                  final ForecastCheckerResultListener forecastCheckerResultListener) {
        if (LocationHelper.rightCoordinates(latitude, longitude)) {
            new ForecastIoNetworkServiceTask() {
                @Override
                public void onForecastSuccess(ForecastTable forecastTable) {
                    forecastCheckerResultListener.onForecastSuccess(forecastTable);
                }
                @Override
                public void onForecastFailure() {
                    forecastCheckerResultListener.onForecastFailure(new ForecastCheckerException());
                }
            }.execute(new ForecastIoRequest(latitude, longitude).getRequest());
        }
    }

    public static interface ForecastCheckerResultListener {
        public void onForecastSuccess(ForecastTable forecastTable);
        public void onForecastFailure(ForecastCheckerException exception);
    }

    public static class ForecastCheckerException extends Exception {
    }
}
