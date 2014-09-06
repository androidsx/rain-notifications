package com.androidsx.rainnotifications.forecast_io;

import android.content.Context;
import android.util.Log;

import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientExecutor;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.ForecastTableBuilder;
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
public final class ForecastIoNetworkServiceTask extends NetworkServiceTask implements WeatherClientExecutor {
    private static final String TAG = ForecastIoNetworkServiceTask.class.getSimpleName();
    private WeatherClientResponseListener responseListener;

    @Override
    public void execute(Context context, double latitude, double longitude, WeatherClientResponseListener responseListener) {
        this.responseListener = responseListener;
        this.execute(new ForecastIoRequest(latitude, longitude).getRequest());
    }

    @Override
    protected void onPostExecute(INetworkResponse rawNetworkResponse) {
        if (rawNetworkResponse == null || rawNetworkResponse.getStatus() == NetworkResponse.Status.FAIL) {
            responseListener.onForecastFailure(new WeatherClientException("Failed to read data from Forecast.io: " + rawNetworkResponse));
        } else {
            final ForecastService.Response response = (ForecastService.Response) rawNetworkResponse;
            Log.v(TAG, "Raw response from Forecast.io:\n" + response);

            final ForecastTable forecastTable = ForecastTableBuilder.buildFromForecastIo(response);
            Log.d(TAG, "Transition table: " + forecastTable);

            responseListener.onForecastSuccess(forecastTable);
        }
    }
}
