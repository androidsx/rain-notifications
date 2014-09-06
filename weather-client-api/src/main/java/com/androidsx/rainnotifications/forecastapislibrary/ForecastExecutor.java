package com.androidsx.rainnotifications.forecastapislibrary;

import android.content.Context;

public interface ForecastExecutor {
    public void execute(Context context, double latitude, double longitude, ForecastResponseListener responseListener);
}
