package com.androidsx.rainnotifications.forecast_io;

import android.util.Log;

import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.ForecastTableBuilder;
import com.forecast.io.network.requests.INetworkRequest;
import com.forecast.io.network.responses.INetworkResponse;
import com.forecast.io.network.responses.NetworkResponse;
import com.forecast.io.toolbox.NetworkServiceTask;
import com.forecast.io.v2.network.services.ForecastService;

/**
 * Async task that perform a request to Forecast.io and returns the result represented by our model
 * objects, namely our {@link ForecastTable}.
 * <p/>
 * Just execute this async task and implement the abstract methods to get your results.
 */
public abstract class ForecastIoNetworkServiceTask extends NetworkServiceTask {
    private static final String TAG = ForecastIoNetworkServiceTask.class.getSimpleName();

    @Override
    protected void onPostExecute(INetworkResponse rawNetworkResponse) {
        if (rawNetworkResponse == null || rawNetworkResponse.getStatus() == NetworkResponse.Status.FAIL) {
            onFailure();
        } else {
            final ForecastService.Response response = (ForecastService.Response) rawNetworkResponse;
            //Log.v(TAG, "Raw response from Forecast.io:\n" + response);

            onSuccess(ForecastTableBuilder.buildFromForecastIo(response));
        }
    }

    /**
     * The network call was successful, and the response is ready to be used. Note that this method
     * is executed in the UI thread.
     *
     * @param forecastTable table of forecasts
     */
    protected abstract void onSuccess(ForecastTable forecastTable);

    /**
     * The network call to Forecast.io failed, or the results failed to parse. The actual reason is,
     * unfortunately, unknown. So... good luck!
     */
    protected abstract void onFailure();
}
