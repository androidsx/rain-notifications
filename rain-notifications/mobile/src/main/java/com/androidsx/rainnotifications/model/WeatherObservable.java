package com.androidsx.rainnotifications.model;

import java.util.Observable;

import com.forecast.io.network.responses.INetworkResponse;
import com.forecast.io.network.responses.NetworkResponse;
import com.forecast.io.toolbox.NetworkServiceTask;
import com.forecast.io.v2.network.services.ForecastService.Builder;
import com.forecast.io.v2.network.services.ForecastService.Request;
import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.LatLng;

import com.androidsx.rainnotifications.util.Constants.ForecastIO;

public class WeatherObservable extends Observable {

    private static final String TAG = WeatherObservable.class.getSimpleName();

    public void checkForecast(Double latitude, Double longitude) {
        new NetworkServiceTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute( INetworkResponse network ) {
                if ( network == null || network.getStatus() == NetworkResponse.Status.FAIL ) {
                    return;
                }

                Response response = (Response) network;
                notifyWeatherChange(response);
            }
        }.execute( getRequest(latitude, longitude) );
    }

    private Request getRequest(Double latitude, Double longitude) {
        LatLng.Builder builderL = LatLng.newBuilder();
        builderL.setLatitude(latitude)
                .setLongitude(longitude)
                .build();
        LatLng latlng = new LatLng(builderL);
        Builder builderF = Request.newBuilder( ForecastIO.API_KEY );
        builderF.setLatLng(latlng).build();

        return new Request(builderF);
    }

    private void notifyWeatherChange(Response response) {
        setChanged();
        notifyObservers(response);
        clearChanged();
    }
}
