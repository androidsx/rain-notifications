package com.androidsx.rainnotifications.forecastapislibrary;

import com.androidsx.rainnotifications.model.ForecastTable;

public interface ForecastApis {

    /**
     * The network call was successful, and the response is ready to be used. Note that this method
     * is executed in the UI thread.
     *
     * @param forecastTable table of forecasts
     */
    public void onRequestSuccess(ForecastTable forecastTable);

    /**
     * The network call to Forecast.io failed, or the results failed to parse. The actual reason is,
     * unfortunately, unknown. So... good luck!
     */
    public void onRequestFailure();
}
