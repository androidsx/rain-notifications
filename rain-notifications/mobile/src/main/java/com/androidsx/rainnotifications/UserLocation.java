package com.androidsx.rainnotifications;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public abstract class UserLocation implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = UserLocation.class.getSimpleName();

    private LocationClient mLocationClient;
    private Context context;

    public UserLocation(Context context) {
        this.context = context;
    }

    public void getUserLocation() {
        mLocationClient = new LocationClient(context, this, this);
        mLocationClient.connect();
    }

    public abstract void obtainedLocation(Location loc);

    @Override
    public void onConnected(Bundle bundle) {
        if(mLocationClient.isConnected()) {
            Location loc = mLocationClient.getLastLocation();
            if(loc != null) {
                obtainedLocation(loc);
            } else {
                // TODO: probably notify to user, that the gps is disabled or not available,
                // if we try to obtain many times the location.
            }
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
