package com.androidsx.rain_notifications.Models;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.LocationClient;

import java.util.Observable;

public class LocationObservable extends Observable {

    private static final String TAG = LocationObservable.class.getSimpleName();

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final String GPS = LocationManager.GPS_PROVIDER;
    private static final String NETWORK = LocationManager.NETWORK_PROVIDER;
    private static final int AVAILABLE = LocationProvider.AVAILABLE;
    private static final int OUT_OF_SERVICE = LocationProvider.OUT_OF_SERVICE;
    private static final int TEMPORARILY_UNAVAILABLE = LocationProvider.TEMPORARILY_UNAVAILABLE;

    private LocationManager mLocationManager;
    private LocationListener mLocationListenerNetwork;
    private LocationListener mLocationListenerGPS;
    private Location actualLocation = null;

    private long minTimeGPS;
    private long minTimeNetwork;
    private float minDist;

    public LocationObservable(Context context, long minTimeGPS, long minTimeNetwork, float minDist) {
        this.minTimeGPS = minTimeGPS;
        this.minTimeNetwork = minTimeNetwork;
        this.minDist = minDist;

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerGPS = getNewLocationListener();
        mLocationListenerNetwork = getNewLocationListener();
        registerProvider(GPS, minTimeGPS, mLocationListenerGPS);
        registerProvider(NETWORK, minTimeNetwork, mLocationListenerNetwork);
    }

    private LocationListener getNewLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(isBetterLocation(location, actualLocation)) {
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
        setChanged();
        notifyObservers(location);
        Log.d(TAG, "Notify New Location: " + location.getProvider());
        clearChanged();
    }

    private void enableLocationUpdates(String provider) {
        if(provider.equals(GPS) && isProviderEnabled(NETWORK)) {
            registerProvider(NETWORK, minTimeNetwork, mLocationListenerNetwork);
        } else if(provider.equals(NETWORK) && isProviderEnabled(GPS)) {
            registerProvider(GPS, minTimeGPS, mLocationListenerGPS);
        }
        Log.d(TAG, "enableLocationUpdates: " + provider);
    }

    private void disableLocationUpdates(String provider) {
        if(provider.equals(GPS) && isProviderEnabled(NETWORK)) {
            registerProvider(NETWORK, minTimeNetwork, mLocationListenerNetwork);
        } else if(provider.equals(NETWORK) && isProviderEnabled(GPS)) {
            registerProvider(GPS, minTimeGPS, mLocationListenerGPS);
        }
        Log.d(TAG, "disableLocationUpdates: " + provider);
    }

    private void statusChanged(String provider, int state, Bundle bundle) {
        updateProviderState(provider, state);
        Log.d(TAG, "statusChanged - Status: " + provider + " : " + state);
    }

    private void registerProvider(String provider, long time, LocationListener listener) {
        mLocationManager.requestLocationUpdates(provider, time, minDist, listener);
    }

    private void unRegisterProvider(LocationListener listener) {
        if(listener != null) mLocationManager.removeUpdates(listener);
    }

    private boolean isProviderEnabled(String provider){
        return mLocationManager.isProviderEnabled(provider);
    }

    private void updateProviderState(String provider, int state) {
        if(provider.equals(GPS)) {

            if(state == AVAILABLE && !isProviderEnabled(NETWORK)) {
                registerProvider(GPS, minTimeGPS, mLocationListenerGPS);
                unRegisterProvider(mLocationListenerNetwork);

            } else if(state == AVAILABLE && isProviderEnabled(NETWORK)) {
                registerProvider(GPS, minTimeGPS, mLocationListenerGPS);
                registerProvider(NETWORK, minTimeNetwork, mLocationListenerNetwork);

            } else if(state == OUT_OF_SERVICE && isProviderEnabled(NETWORK)) {
                unRegisterProvider(mLocationListenerGPS);
                registerProvider(NETWORK, minTimeNetwork, mLocationListenerNetwork);

            } else if(state == TEMPORARILY_UNAVAILABLE && isProviderEnabled(NETWORK)) {
                unRegisterProvider(mLocationListenerGPS);
                registerProvider(NETWORK, minTimeNetwork, mLocationListenerNetwork);
            }
        } else if(provider.equals(NETWORK)) {

            if(state == AVAILABLE && !isProviderEnabled(GPS)) {
                registerProvider(NETWORK, minTimeNetwork, mLocationListenerNetwork);
                unRegisterProvider(mLocationListenerGPS);

            } else if(state == AVAILABLE && isProviderEnabled(GPS)) {
                registerProvider(NETWORK, minTimeNetwork, mLocationListenerNetwork);
                registerProvider(GPS, minTimeGPS, mLocationListenerGPS);

            } else if(state == OUT_OF_SERVICE && isProviderEnabled(GPS)) {
                unRegisterProvider(mLocationListenerNetwork);
                registerProvider(GPS, minTimeGPS, mLocationListenerGPS);

            } else if(state == TEMPORARILY_UNAVAILABLE && isProviderEnabled(GPS)) {
                unRegisterProvider(mLocationListenerNetwork);
                registerProvider(GPS, minTimeGPS, mLocationListenerGPS);
            }
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
