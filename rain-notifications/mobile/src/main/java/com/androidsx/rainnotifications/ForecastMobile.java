package com.androidsx.rainnotifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Locale;
import java.util.Observer;
import java.util.Observable;

import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidsx.rainnotifications.Services.WeatherService;
import com.androidsx.rainnotifications.Utils.Constants;
import com.androidsx.rainnotifications.Utils.NotificationHelper;
import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataPoint;

import com.androidsx.rainnotifications.Models.LocationObservable;
import com.androidsx.rainnotifications.Models.WeatherObservable;
import com.androidsx.rainnotifications.Utils.AddressHelper;
import com.androidsx.rainnotifications.Utils.DateHelper;
import com.androidsx.rainnotifications.Utils.AnalyzerHelper;
import com.androidsx.rainnotifications.Utils.Constants.Time;
import com.androidsx.rainnotifications.Utils.Constants.Localization;
import com.androidsx.rainnotifications.Utils.Constants.ForecastIO.Icon;
import com.androidsx.rainnotifications.Services.ScheduleService;

public class ForecastMobile extends Activity implements Observer, View.OnClickListener/*, DataApi.DataListener*/ {

    private static final String TAG = ForecastMobile.class.getSimpleName();

    private SharedPreferences shared;

    private Button btn_call;
    private TextView txt_response;
    private TextView txt_city;
    private TextView txt_update;

    //private GoogleApiClient mGoogleApiClient = getGoogleApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        Intent i = new Intent(this, WeatherService.class);
        startService(i);

        setupUI();
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    private void setupUI() {
        btn_call = (Button) findViewById(R.id.btn_call);
        btn_call.setOnClickListener(this);
        btn_call.setVisibility(View.GONE);
        txt_response = (TextView) findViewById(R.id.txt_response);
        txt_city = (TextView) findViewById(R.id.txt_city);
        txt_update = (TextView) findViewById(R.id.txt_update);

        shared = getSharedPreferences(WeatherService.SHARED_WEATHER, 0);
        txt_city.setText(shared.getString(Constants.SharedPref.LOCATION, "Unknown Location"));
        txt_update.setText(shared.getString(Constants.SharedPref.CURRENTLY, ""));
        txt_response.setText(shared.getString(Constants.SharedPref.HISTORY, ""));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //locationObservable.deleteObserver(this);
        //locationObservable = null;
    }

    @Override
    public void onClick(View view) {

    }

    /*private void sendToWatch(String summary, String icon, String deltaTime, String forecastTime) {
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

    /*private GoogleApiClient getGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
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
                .build();
    }*/
}

