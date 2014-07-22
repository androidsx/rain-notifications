package com.androidsx.rainnotifications.forecast_io;

import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.ForecastTableBuilder;
import com.forecast.io.network.responses.NetworkResponse;
import com.forecast.io.v2.network.services.ForecastService;

public class ForecastIoResponse {

    private ForecastService.Response response;

    public ForecastIoResponse (ForecastService.Response res) {
        this.response = res;
    }

    public ForecastTable getForecastTable() {
        return ForecastTableBuilder.buildFromForecastIo(response);
    }
}
