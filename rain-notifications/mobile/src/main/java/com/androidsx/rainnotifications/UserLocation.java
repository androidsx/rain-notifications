package com.androidsx.rainnotifications;

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
 * Class that determine de user location in coordinates and text.
 */
public class UserLocation {

    private final LocationClient mLocationClient;

    public UserLocation(final Context context, final UserLocationResultListener userLocationResultListener) {
        mLocationClient = new LocationClient(context, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                if (mLocationClient.isConnected()) {
                    Location loc = mLocationClient.getLastLocation();
                    if (loc != null) {
                        userLocationResultListener.onLocationSuccess(loc);
                    } else {
                        userLocationResultListener.onLocationFailure(new UserLocationException());
                    }
                }
            }

            @Override
            public void onDisconnected() {

            }
        }, new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        });
    }

    public void connect() {
        mLocationClient.connect();
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
}
