package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.model.ForecastTable;

public interface ForecastResultListener {
    public void onForecastSuccess(ForecastTable forecastTable, String address);
    public void onForecastFailure(ForecastException exception);
}
