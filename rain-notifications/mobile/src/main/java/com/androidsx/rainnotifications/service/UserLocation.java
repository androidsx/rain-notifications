package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.util.LocationHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
