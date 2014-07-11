package com.androidsx.rainnotifications.Models;

import android.util.Log;
import java.util.Observable;

import com.forecast.io.network.responses.INetworkResponse;
import com.forecast.io.network.responses.NetworkResponse;
import com.forecast.io.toolbox.NetworkServiceTask;
import com.forecast.io.v2.network.services.ForecastService.Builder;
import com.forecast.io.v2.network.services.ForecastService.Request;
import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.LatLng;

import com.androidsx.rainnotifications.Utils.Constants.ForecastIO;

public class WeatherObservable extends Observable {

    private static final String TAG = WeatherObservable.class.getSimpleName();

    public void getWeather(Double latitude, Double longitude, int timeAgo) {
        LatLng.Builder builderL = LatLng.newBuilder();
        builderL.setLatitude(latitude)
                .setLongitude(longitude)
                //.setTime(System.currentTimeMillis() / 1000 + (timeAgo / 1000) + 60) //Time in seconds
                .build();
        LatLng latlng = new LatLng(builderL);

        Builder builderF = Request.newBuilder( ForecastIO.API_KEY );
        builderF.setLatLng(latlng).build();
        Request request = new Request(builderF);

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

                //TODO: For notify when weather match in our rules
                notifyWeatherChange(response);
            }

        }.execute( request );
    }

    private void notifyWeatherChange(Response response) {
        setChanged();
        notifyObservers(response);
        Log.d(TAG, "Notify New Weather: ");
        clearChanged();
    }
}
