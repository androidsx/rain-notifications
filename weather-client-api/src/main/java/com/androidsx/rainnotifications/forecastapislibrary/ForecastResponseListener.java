package com.androidsx.rainnotifications.forecastapislibrary;

import android.content.Context;

import com.androidsx.rainnotifications.model.ForecastTable;

/**
 * Listener for a response from a forecast service.
 */
public interface ForecastResponseListener {

    public void execute(Context context, double latitude, double longitude);

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
