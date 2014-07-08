package com.androidsx.rain_notifications;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.androidsx.rain_notifications.Services.LocationService;
import com.androidsx.rain_notifications.Services.WeatherService;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class forecast_mobile extends Activity implements View.OnClickListener/*, DataApi.DataListener*/ {

    private static final String TAG = forecast_mobile.class.getSimpleName();

    private static final String API_KEY = "f1fd27e70564bd6765bf40b3497cbf4f";
    private static final String NEW_YORK_CITY = "New York City";
    private static final Double NEW_YORK_LAT = 40.72228267283148;
    private static final Double NEW_YORK_LON = -73.9434814453125;
    private static final String EXTRA_FORECAST = "FORECAST";

    private static final long LOCATION_TIMEOUT_SECONDS = 20;

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

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

        setupUI();
    }

    private void setupUI() {
        btn_call = (Button) findViewById(R.id.btn_call);
        btn_call.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        weatherRequest(this);
    }

    private void weatherRequest(final Context context) {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final LocationService locationService = new LocationService(locationManager);

        final Observable location = locationService.getLocation()
        .timeout(LOCATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .flatMap(new Func1<Location, Observable<?>>() {
            @Override
            public Observable<?> call(Location location) {
                final double longitude = location.getLongitude();
                final double latitude = location.getLatitude();

                HashMap latLng = new HashMap();
                latLng.put("Latitud", latitude);
                latLng.put("Longitud", longitude);

                Log.d(TAG, "Observable...");
                return Observable.from(latLng);
            }
        });

        location
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<HashMap>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "Completed...");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(HashMap hashMap) {
                Log.d(TAG, hashMap.get("Latitud").toString() + " - " + hashMap.get("Longitud").toString());
            }
        });
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

