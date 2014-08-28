package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.model.ForecastTable;

public interface ForecastCheckerResultListener {
    public void onForecastSuccess(ForecastTable forecastTable, String address);
    public void onForecastFailure(ForecastCheckerException exception);
}
