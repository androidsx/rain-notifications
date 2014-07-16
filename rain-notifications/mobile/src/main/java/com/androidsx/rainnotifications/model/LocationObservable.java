package com.androidsx.rainnotifications.model;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import java.util.Observable;

import com.androidsx.rainnotifications.util.LocationHelper;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.location.LocationProvider.AVAILABLE;
import static android.location.LocationProvider.OUT_OF_SERVICE;
import static android.location.LocationProvider.TEMPORARILY_UNAVAILABLE;

public class LocationObservable extends Observable {

    private static final String TAG = LocationObservable.class.getSimpleName();

    private LocationListener mLocationListenerNetwork;
    private LocationListener mLocationListenerGPS;
    private Location actualLocation = null;
    private LocationHelper locationHelper;

    private long minTimeGPS;
    private long minTimeNetwork;
    private float minDist;

    public LocationObservable(Context context, long minTimeGPS, long minTimeNetwork, float minDist) {
        locationHelper = new LocationHelper((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));

        this.minTimeGPS = minTimeGPS;
        this.minTimeNetwork = minTimeNetwork;
        this.minDist = minDist;
    }

    private LocationListener getNewLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(locationHelper.isBetterLocation(location, actualLocation)) {
                    actualLocation = location;
                    notifyLocationChange(location);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                statusChanged(s, i, bundle);
            }

            @Override
            public void onProviderEnabled(String s) {
                enableLocationUpdates(s);
            }

            @Override
            public void onProviderDisabled(String s) {
                disableLocationUpdates(s);
            }
        };
    }

    private void notifyLocationChange(Location location) {
        Log.d(TAG, "Notify New Location: " + location.getProvider());
        setChanged();
        notifyObservers(location);
        clearChanged();
    }

    public void startLocationListeners() {
        mLocationListenerGPS = getNewLocationListener();
        mLocationListenerNetwork = getNewLocationListener();
        enableLocationUpdates(NETWORK_PROVIDER);
        enableLocationUpdates(GPS_PROVIDER);
    }

    public void stopLocationListeners() {
        locationHelper.unRegisterProvider(mLocationListenerGPS);
        locationHelper.unRegisterProvider(mLocationListenerNetwork);
    }

    private void enableLocationUpdates(String provider) {
        if(provider.equals(GPS_PROVIDER) && locationHelper.isProviderEnabled(NETWORK_PROVIDER)) {
            locationHelper.registerProvider(NETWORK_PROVIDER, minTimeNetwork, minDist, mLocationListenerNetwork);
        } else if(provider.equals(NETWORK_PROVIDER) && locationHelper.isProviderEnabled(GPS_PROVIDER)) {
            locationHelper.registerProvider(GPS_PROVIDER, minTimeGPS, minDist, mLocationListenerGPS);
        }
        Log.d(TAG, "enableLocationUpdates: " + provider);
    }

    private void disableLocationUpdates(String provider) {
        if(provider.equals(GPS_PROVIDER) && locationHelper.isProviderEnabled(NETWORK_PROVIDER)) {
            locationHelper.registerProvider(NETWORK_PROVIDER, minTimeNetwork, minDist, mLocationListenerNetwork);
        } else if(provider.equals(NETWORK_PROVIDER) && locationHelper.isProviderEnabled(GPS_PROVIDER)) {
            locationHelper.registerProvider(GPS_PROVIDER, minTimeGPS, minDist, mLocationListenerGPS);
        }
        Log.d(TAG, "disableLocationUpdates: " + provider);
    }

    private void statusChanged(String provider, int state, Bundle bundle) {
        updateProviderState(provider, state);
        Log.d(TAG, "statusChanged - Status: " + provider + " : " + state);
    }

    private void updateProviderState(String provider, int state) {
        if(provider.equals(GPS_PROVIDER)) {

            if(state == AVAILABLE && !locationHelper.isProviderEnabled(NETWORK_PROVIDER)) {
                locationHelper.registerProvider(GPS_PROVIDER, minTimeGPS, minDist, mLocationListenerGPS);
                locationHelper.unRegisterProvider(mLocationListenerNetwork);

            } else if(state == AVAILABLE && locationHelper.isProviderEnabled(NETWORK_PROVIDER)) {
                locationHelper.registerProvider(GPS_PROVIDER, minTimeGPS, minDist, mLocationListenerGPS);
                locationHelper.registerProvider(NETWORK_PROVIDER, minTimeNetwork, minDist, mLocationListenerNetwork);

            } else if(state == OUT_OF_SERVICE && locationHelper.isProviderEnabled(NETWORK_PROVIDER)) {
                locationHelper.unRegisterProvider(mLocationListenerGPS);
                locationHelper.registerProvider(NETWORK_PROVIDER, minTimeNetwork, minDist, mLocationListenerNetwork);

            } else if(state == TEMPORARILY_UNAVAILABLE && locationHelper.isProviderEnabled(NETWORK_PROVIDER)) {
                locationHelper.unRegisterProvider(mLocationListenerGPS);
                locationHelper.registerProvider(NETWORK_PROVIDER, minTimeNetwork, minDist, mLocationListenerNetwork);
            }
        } else if(provider.equals(NETWORK_PROVIDER)) {

            if(state == AVAILABLE && !locationHelper.isProviderEnabled(GPS_PROVIDER)) {
                locationHelper.registerProvider(NETWORK_PROVIDER, minTimeNetwork, minDist, mLocationListenerNetwork);
                locationHelper.unRegisterProvider(mLocationListenerGPS);

            } else if(state == AVAILABLE && locationHelper.isProviderEnabled(GPS_PROVIDER)) {
                locationHelper.registerProvider(NETWORK_PROVIDER, minTimeNetwork, minDist, mLocationListenerNetwork);
                locationHelper.registerProvider(GPS_PROVIDER, minTimeGPS, minDist, mLocationListenerGPS);

            } else if(state == OUT_OF_SERVICE && locationHelper.isProviderEnabled(GPS_PROVIDER)) {
                locationHelper.unRegisterProvider(mLocationListenerNetwork);
                locationHelper.registerProvider(GPS_PROVIDER, minTimeGPS, minDist, mLocationListenerGPS);

            } else if(state == TEMPORARILY_UNAVAILABLE && locationHelper.isProviderEnabled(GPS_PROVIDER)) {
                locationHelper.unRegisterProvider(mLocationListenerNetwork);
                locationHelper.registerProvider(GPS_PROVIDER, minTimeGPS, minDist, mLocationListenerGPS);
            }
        }
    }
}
