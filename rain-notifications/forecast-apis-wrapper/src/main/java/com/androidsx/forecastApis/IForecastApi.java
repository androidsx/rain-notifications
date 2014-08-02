package com.androidsx.forecastApis;

import com.androidsx.rainnotifications.model.ForecastTable;

public interface IForecastApi {
    public void callToApi(double latitude, double longitude);
    public void onCallBackSuccess(ForecastTable forecastTable);
    public void onCallBackFailure();
}
