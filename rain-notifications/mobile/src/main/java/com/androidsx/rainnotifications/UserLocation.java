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

public class UserLocation implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = UserLocation.class.getSimpleName();

    private LocationClient mLocationClient;
    private ForecastLocationResultListener mForecastLocationResultListener;
    private Context context;

    public UserLocation(Context context, ForecastLocationResultListener forecastLocationResultListener) {
        this.context = context;
        mLocationClient = new LocationClient(context, this, this);
        mForecastLocationResultListener = forecastLocationResultListener;
    }

    public void determineLocation() {
        mLocationClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mLocationClient.isConnected()) {
            Location loc = mLocationClient.getLastLocation();
            if (loc != null) {
                mForecastLocationResultListener.onLocationSuccess(loc, getLocationAddress(context, loc.getLatitude(), loc.getLongitude()));
            } else {
                mForecastLocationResultListener.onLocationFailure(new ForecastLocationException());
            }
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
