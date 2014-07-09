package com.androidsx.rain_notifications;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.androidsx.rain_notifications.Models.LocationObservable;
import com.androidsx.rain_notifications.Services.WeatherService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Observer;
import java.util.Observable;

public class forecast_mobile extends Activity implements Observer, View.OnClickListener/*, DataApi.DataListener*/ {

    private static final String TAG = forecast_mobile.class.getSimpleName();

    private static final String API_KEY = "f1fd27e70564bd6765bf40b3497cbf4f";
    private static final String NEW_YORK_CITY = "New York City";
    private static final Double NEW_YORK_LAT = 40.72228267283148;
    private static final Double NEW_YORK_LON = -73.9434814453125;
    private static final Integer TIME_AGO = 3660;
    private static final String EXTRA_FORECAST = "FORECAST";

    private static final long LOCATION_GPS_TIMEOUT = 1 * 30 * 1000;
    private static final long LOCATION_NETWORK_TIMEOUT = 2 * 60 * 1000;
    private static final long LOCATION_DISTANCE = 0;

    private LocationObservable locationObservable;
    private Button btn_call;

    /*private GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle connectionHint) {
                    Log.d(TAG, "onConnected: " + connectionHint);
                }
                @Override
                public void onConnectionSuspended(int cause) {
                    Log.d(TAG, "onConnectionSuspended: " + cause);
                }
            })
            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {
                    Log.d(TAG, "onConnectionFailed: " + result);
                }
            })
            .addApi(Wearable.API)
            .build();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        locationObservable =
                new LocationObservable(this, LOCATION_GPS_TIMEOUT, LOCATION_NETWORK_TIMEOUT, LOCATION_DISTANCE);
        locationObservable.addObserver(this);

        setupUI();
    }

    @Override
    public void update(Observable observable, Object o) {
        Location location = (Location) o;

        String address = "Uknown Location";

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            address = addresses.get(0).getLocality();
        }

        /*WeatherService.getWeather(
                this,
                address,
                location.getLatitude(),
                location.getLongitude(),
                TIME_AGO);*/

        Log.d(TAG, "Observer update...");
    }

    private void setupUI() {
        btn_call = (Button) findViewById(R.id.btn_call);
        btn_call.setOnClickListener(this);
        btn_call.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationObservable.deleteObserver(this);
        locationObservable = null;
    }

    @Override
    public void onClick(View view) {

    }

    /*private void sendToWatch(String summary) {
        PutDataMapRequest dataMap = PutDataMapRequest.create("/forecast");
        dataMap.getDataMap().putString(EXTRA_FORECAST, summary);

        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, request);

        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                if(dataItemResult.getStatus().isSuccess()) {
                    Log.d(TAG, "Data item set: " + dataItemResult.getDataItem().getUri());
                }
            }
        });
    }*/

    /*@Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forecast_mobile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

