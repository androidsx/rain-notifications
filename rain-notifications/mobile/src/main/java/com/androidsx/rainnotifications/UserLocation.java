package com.androidsx.rainnotifications;

import android.app.Activity;
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
public abstract class UserLocation extends Activity implements UserLocationResultListener {

    private static final String TAG = UserLocation.class.getSimpleName();

    private LocationClient mLocationClient;

    public UserLocation(final Context context) {
        mLocationClient = new LocationClient(context, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                if (mLocationClient.isConnected()) {
                    Location loc = mLocationClient.getLastLocation();
                    if (loc != null) {
                        onLocationSuccess(loc, getLocationAddress(context, loc.getLatitude(), loc.getLongitude()));
                    } else {
                        onLocationFailure(new UserLocationException());
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

    public void determineLocation() {
        mLocationClient.connect();
    }

    /**
     * Method that translate a location coordinates into a quotidian name.
     *
     * @param latitude
     * @param longitude
     * @return String - quotidian name of direction
     */
    public static String getLocationAddress(Context context, double latitude, double longitude) {
        String address = context.getString(R.string.current_name_location);

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            if (addresses.get(0).getSubLocality() != null) address = addresses.get(0).getSubLocality();
            else if (addresses.get(0).getLocality() != null) address = addresses.get(0).getLocality();
            else if (addresses.get(0).getCountryName() != null) address = addresses.get(0).getCountryName();
        }

        return address;
    }
}
