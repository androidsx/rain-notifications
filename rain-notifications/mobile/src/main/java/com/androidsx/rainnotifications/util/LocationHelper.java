package com.androidsx.rainnotifications.util;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.androidsx.rainnotifications.util.Constants.Time;

/*
 * No utilizada con la actual lógica de la app.
 */

public class LocationHelper {

    private LocationManager mLocationManager;

    public LocationHelper(LocationManager locationManager) {
        mLocationManager = locationManager;
    }

    public void registerProvider(String provider, long time, float minDist, LocationListener listener) {
        mLocationManager.requestLocationUpdates(provider, time, minDist, listener);
    }

    public void unRegisterProvider(LocationListener listener) {
        if(listener != null) mLocationManager.removeUpdates(listener);
    }

    public boolean isProviderEnabled(String provider){
        return mLocationManager.isProviderEnabled(provider);
    }

    public boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > Time.ONE_MINUTE_MILLIS * 2;
        boolean isSignificantlyOlder = timeDelta < -Time.ONE_MINUTE_MILLIS * 2;
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
