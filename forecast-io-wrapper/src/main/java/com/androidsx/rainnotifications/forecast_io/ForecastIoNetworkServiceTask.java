package com.androidsx.rainnotifications.forecast_io;

import android.util.Log;

import com.androidsx.rainnotifications.forecastapislibrary.ForecastApis;
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
public abstract class ForecastIoNetworkServiceTask extends NetworkServiceTask implements ForecastApis {
    private static final String TAG = ForecastIoNetworkServiceTask.class.getSimpleName();

    @Override
    protected void onPostExecute(INetworkResponse rawNetworkResponse) {
        if (rawNetworkResponse == null || rawNetworkResponse.getStatus() == NetworkResponse.Status.FAIL) {
            onForecastFailure();
        } else {
            final ForecastService.Response response = (ForecastService.Response) rawNetworkResponse;
            Log.v(TAG, "Raw response from Forecast.io:\n" + response);

            final ForecastTable forecastTable = ForecastTableBuilder.buildFromForecastIo(response);
            Log.d(TAG, "Transition table: " + forecastTable);

            onForecastSuccess(forecastTable);
        }
    }
}
