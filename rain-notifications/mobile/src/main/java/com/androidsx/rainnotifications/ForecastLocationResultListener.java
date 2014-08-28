package com.androidsx.rainnotifications;

import android.location.Location;

public interface ForecastLocationResultListener {
    public void onLocationSuccess(Location location, String address);
    public void onLocationFailure(ForecastLocationException exception);
}
