package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.util.LocationHelper;

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
                protected void onSuccess(ForecastTable forecastTable) {
                    forecastCheckerResultListener.onForecastSuccess(forecastTable);
                }
                @Override
                protected void onFailure() {
                    forecastCheckerResultListener.onForecastFailure(new ForecastCheckerException());
                }
            }.execute(new ForecastIoRequest(latitude, longitude).getRequest());
        }
    }
}
