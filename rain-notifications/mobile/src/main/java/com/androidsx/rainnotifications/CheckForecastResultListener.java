package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.model.ForecastTable;

public interface CheckForecastResultListener {
    public void onForecastSuccess(ForecastTable forecastTable, String address);
    public void onForecastFailure(CheckForecastException exception);
}
