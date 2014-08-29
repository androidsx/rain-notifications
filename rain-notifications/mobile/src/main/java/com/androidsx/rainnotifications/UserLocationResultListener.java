package com.androidsx.rainnotifications;

import android.location.Location;

public interface UserLocationResultListener {
    public void onLocationSuccess(Location location, String address);
    public void onLocationFailure(UserLocationException exception);
}
