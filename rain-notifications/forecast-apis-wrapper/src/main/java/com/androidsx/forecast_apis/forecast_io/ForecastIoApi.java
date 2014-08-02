package com.androidsx.forecast_apis.forecast_io;

import com.androidsx.forecast_apis.IForecastApi;
import com.androidsx.forecast_apis.forecast_io.model.ForecastTableBuilder;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.forecast.io.network.responses.INetworkResponse;
import com.forecast.io.network.responses.NetworkResponse;
import com.forecast.io.toolbox.NetworkServiceTask;
import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.LatLng;

/**
 * Async task that perform a request to Forecast.io and returns the result represented by our model
 * objects, namely our {@link com.androidsx.rainnotifications.model.ForecastTable}.
 * <p/>
 * Just execute this async task and implement the abstract methods to get your results.
 */

public abstract class ForecastIoApi implements IForecastApi {
    @Override
    public void callToApi(double latitude, double longitude) {
        new NetworkServiceTask() {
            @Override
            protected void onPostExecute(INetworkResponse iNetworkResponse) {
                if (iNetworkResponse == null || iNetworkResponse.getStatus() == NetworkResponse.Status.FAIL) {
                    onCallBackFailure();
                } else {
                    final ForecastService.Response response = (ForecastService.Response) iNetworkResponse;
                    onCallBackSuccess(ForecastTableBuilder.buildFromForecastIo(response));
                }
                super.onPostExecute(iNetworkResponse);
            }
        }.execute(getRequest(latitude, longitude));
    }

    @Override
    public abstract void onCallBackSuccess(ForecastTable forecastTable);

    @Override
    public abstract void onCallBackFailure();

    public ForecastService.Request getRequest(double latitude, double longitude) {
        LatLng.Builder builderL = LatLng.newBuilder();
        builderL.setLatitude(latitude)
                .setLongitude(longitude)
                .build();
        LatLng latlng = new LatLng(builderL);
        ForecastService.Builder builderF = ForecastService.Request.newBuilder(Constants.API_KEY);
        builderF.setLatLng(latlng).build();

        return new ForecastService.Request(builderF);
    }
}
