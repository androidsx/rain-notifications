package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.Models.WeatherObservable;
import com.androidsx.rainnotifications.Services.WeatherService;
import com.androidsx.rainnotifications.Utils.Constants;

public class ForecastMobile extends Activity implements View.OnClickListener/*, DataApi.DataListener*/ {

    private static final String TAG = ForecastMobile.class.getSimpleName();

    private Button btn_call;
    private Button btn_refresh;
    private TextView txt_response;
    private TextView txt_city;
    private TextView txt_update;
    private ImageView icon;
    private ImageView update_icon;

    private SharedPreferences shared;

    //private GoogleApiClient mGoogleApiClient = getGoogleApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        setupUI();
    }

    private void setupUI() {
        shared = getSharedPreferences(WeatherService.SHARED_WEATHER, 0);

        btn_call = (Button) findViewById(R.id.btn_call);
        btn_call.setOnClickListener(this);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(this);

        txt_response = (TextView) findViewById(R.id.txt_response);
        txt_city = (TextView) findViewById(R.id.txt_city);
        txt_update = (TextView) findViewById(R.id.txt_update);
        icon = (ImageView) findViewById(R.id.icon);
        update_icon = (ImageView) findViewById(R.id.update_icon);

        txt_city.setText(shared.getString(Constants.SharedPref.LOCATION, ""));
        txt_update.setText(shared.getString(Constants.SharedPref.CURRENTLY, ""));
        txt_response.setText(shared.getString(Constants.SharedPref.HISTORY, ""));
        String ic = shared.getString(Constants.SharedPref.ICON, "");
        icon.setImageDrawable(getResources().getDrawable(getIcon(ic)));
        ic = shared.getString(Constants.SharedPref.UPDATE_ICON, "");
        update_icon.setImageDrawable(getResources().getDrawable(getIcon(ic)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btn_call)) {
            Intent i = new Intent(this, WeatherService.class);
            startService(i);
            btn_call.setVisibility(View.GONE);
        } else {
            String ic = shared.getString(Constants.SharedPref.ICON, "");
            txt_city.setText(shared.getString(Constants.SharedPref.LOCATION, ""));
            txt_update.setText(shared.getString(Constants.SharedPref.CURRENTLY, ""));
            txt_response.setText(shared.getString(Constants.SharedPref.HISTORY, ""));
            icon.setImageDrawable(getResources().getDrawable(getIcon(ic)));
            ic = shared.getString(Constants.SharedPref.UPDATE_ICON, "");
            update_icon.setImageDrawable(getResources().getDrawable(getIcon(ic)));
        }

    }

    private int getIcon(String icon) {
        if(icon.equals(Constants.ForecastIO.Icon.RAIN)) {
            return R.drawable.rain;
        } else if(icon.equals(Constants.ForecastIO.Icon.CLEAR_DAY)) {
            return R.drawable.clear_day;
        } else if(icon.equals(Constants.ForecastIO.Icon.CLEAR_NIGHT)) {
            return R.drawable.clear_night;
        } else if(icon.equals(Constants.ForecastIO.Icon.CLOUDY)) {
            return R.drawable.cloudy;
        } else if(icon.equals(Constants.ForecastIO.Icon.PARTLY_CLOUDY_DAY)) {
            return R.drawable.partly_cloudy_day;
        } else if(icon.equals(Constants.ForecastIO.Icon.PARTLY_CLOUDY_NIGHT)) {
            return R.drawable.partly_cloudy_night;
        } else if(icon.equals(Constants.ForecastIO.Icon.SNOW)) {
            return R.drawable.snow;
        } else if(icon.equals(Constants.ForecastIO.Icon.THUNDERSTORM)) {
            return R.drawable.thunderstorm;
        } else if(icon.equals(Constants.ForecastIO.Icon.HAIL)) {
            return R.drawable.hail;
        } else {
            return R.drawable.unknown;
        }
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

