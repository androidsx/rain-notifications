package com.androidsx.rainnotifications.backgroundservice.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Helper class to fetch the current location of the user.
 */
public class UserLocationFetcher {

    public static void getUserLocation(final Context context, final UserLocationResultListener userLocationResultListener) {
        new UserLocation(context, userLocationResultListener);
    }

    /**
     * Translates a location coordinates into a quotidian name.
     *
     * @return quotidian name of direction or null if it couldn't be retrieved
     */
    public static String getLocationAddress(Context context, double latitude, double longitude) {

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String address;
        if (addresses != null && addresses.size() > 0) {
            if (addresses.get(0).getSubLocality() != null) address = addresses.get(0).getSubLocality();
            else if (addresses.get(0).getLocality() != null) address = addresses.get(0).getLocality();
            else if (addresses.get(0).getCountryName() != null) address = addresses.get(0).getCountryName();
            else address = null;
        } else {
            address = null;
        }

        return address;
    }

    private static class UserLocation implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
        private final LocationClient locationClient;
        private final UserLocationResultListener userLocationResultListener;

        public UserLocation(final Context context, final UserLocationResultListener userLocationResultListener) {
            this.userLocationResultListener = userLocationResultListener;
            locationClient = new LocationClient(context, this, this);
            locationClient.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {
            sendLocation(locationClient.getLastLocation());
            locationClient.disconnect();
        }

        @Override
        public void onDisconnected() {
            sendLocation(null);
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            sendLocation(null);
        }

        private void sendLocation(Location location) {
            if(location != null) {
                userLocationResultListener.onLocationSuccess(location);
            }
            else {
                userLocationResultListener.onLocationFailure(new UserLocationException());
            }
        }
    }

    public static interface UserLocationResultListener {
        public void onLocationSuccess(Location location);
        public void onLocationFailure(UserLocationException exception);
    }

    public static class UserLocationException extends Exception {
    }
}
