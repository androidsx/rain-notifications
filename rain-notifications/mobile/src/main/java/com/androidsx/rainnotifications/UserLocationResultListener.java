package com.androidsx.rainnotifications;

import android.location.Location;

public interface UserLocationResultListener {
    public void onLocationSuccess(Location location);
    public void onLocationFailure(UserLocationException exception);
}
