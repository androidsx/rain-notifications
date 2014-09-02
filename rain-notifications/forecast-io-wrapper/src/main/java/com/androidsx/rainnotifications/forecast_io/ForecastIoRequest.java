package com.androidsx.rainnotifications.forecast_io;

import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.LatLng;

public class ForecastIoRequest {

    private double latitude;
    private double longitude;

    public ForecastIoRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ForecastService.Request getRequest() {
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
