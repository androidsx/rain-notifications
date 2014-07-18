package com.androidsx.rainnotifications.model;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.util.Observable;

public class LocationObservable extends Observable implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = LocationObservable.class.getSimpleName();

    private LocationClient mLocationClient;
    private Context context;

    public LocationObservable(Context context) {
        this.context = context;
    }

    public void getLastLocation() {
        mLocationClient = new LocationClient(context, this, this);
        mLocationClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mLocationClient.isConnected()) {
            notifyLocationChange(mLocationClient.getLastLocation());
        }
    }

    private void notifyLocationChange(Location location) {
        Log.d(TAG, "Notify New Location: " + location.getProvider());

        setChanged();
        notifyObservers(location);
        clearChanged();

        mLocationClient.disconnect();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
