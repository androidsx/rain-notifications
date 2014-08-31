package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.model.ForecastTable;

public interface ForecastCheckerResultListener {
    public void onForecastSuccess(ForecastTable forecastTable);
    public void onForecastFailure(ForecastCheckerException exception);
}
