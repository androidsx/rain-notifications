package com.androidsx.rainnotifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataPoint;

import com.androidsx.rainnotifications.Models.LocationObservable;
import com.androidsx.rainnotifications.Models.WeatherObservable;
import com.androidsx.rainnotifications.Utils.AddressHelper;
import com.androidsx.rainnotifications.Utils.DateHelper;
import com.androidsx.rainnotifications.Utils.AnalyzerHelper;
import com.androidsx.rainnotifications.Utils.Constants.Time;
import com.androidsx.rainnotifications.Utils.Constants.Distance;
import com.androidsx.rainnotifications.Utils.Constants.Localization;
import com.androidsx.rainnotifications.Utils.Constants.ForecastIO.Icon;
import com.androidsx.rainnotifications.Services.ScheduleService;

public class ForecastMobile extends Activity implements Observer, View.OnClickListener/*, DataApi.DataListener*/ {

    private static final String TAG = ForecastMobile.class.getSimpleName();
    private static long nextApiCallTime = -1;

    public static Location lastLocation;
    private AlarmManager alarmMgr;
    private Intent intent;
    private PendingIntent alarmIntent;

    private LocationObservable locationObservable;
    public static WeatherObservable weatherObservable;
    private Button btn_call;
    private TextView txt_response;
    private TextView txt_city;

    double latitude = Localization.NEW_YORK_LAT;
    double longitude = Localization.NEW_YORK_LON;

    //private GoogleApiClient mGoogleApiClient = getGoogleApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, ScheduleService.class);
        alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        locationObservable =
                new LocationObservable(this, Localization.LOCATION_GPS_TIMEOUT,
                        Localization.LOCATION_NETWORK_TIMEOUT, Localization.LOCATION_DISTANCE);
        locationObservable.addObserver(this);

        weatherObservable = new WeatherObservable();
        weatherObservable.addObserver(this);

        lastLocation = new Location(LocationManager.NETWORK_PROVIDER);
        lastLocation.setLatitude(latitude);
        lastLocation.setLongitude(longitude);

        setupUI();
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass().equals(LocationObservable.class)){
            Location location = (Location) o;

            //lastLocation = location;

            String address = new AddressHelper().getLocationAddress(this,
                    lastLocation.getLatitude(), lastLocation.getLongitude());

            weatherObservable.getWeather(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            txt_city.setText(address);
            Log.d(TAG, "Location Observer update...\nLocation: " + address +
                    " --> lat: " + latitude +
                    " - long: " + longitude);

        } else if(observable.getClass().equals(WeatherObservable.class)) {
            Response response = (Response) o;

            DataPoint currently = response.getForecast().getCurrently();
            DataPoint dpRain;

            ForecastAnalyzer fa = new ForecastAnalyzer();
            fa.setResponse(response);
            dpRain = fa.analyzeForecastForRain(currently.getIcon());

            if(alarmMgr != null) {
                alarmMgr.cancel(alarmIntent);
                alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() +
                                (nextApiCallTime * 1000 - System.currentTimeMillis()),
                        alarmIntent);
            }

            Log.d(TAG, "Weather Observer update..." +
                    "\n");

            writeResult(dpRain, currently, Icon.RAIN);
        }
    }

    private void writeResult(DataPoint dp, DataPoint currently, String icon) {
        String forecast;
        String currentTime = new DateHelper()
                .formatTime(System.currentTimeMillis() / 1000, Time.TIME_FORMAT, Time.TIME_ZONE_NEW_YORK, Locale.US);
        String nextApiCall = new DateHelper()
                .formatTime(nextApiCallTime, Time.TIME_FORMAT, Time.TIME_ZONE_NEW_YORK, Locale.US);
        if(dp == null) {
            forecast = "\nSearching: " + icon + "\n\nCurrently: " + currently.getIcon() +
                    " at "+ currentTime +
                    "\n\nNo changes expected until tomorrow." +
                    "\n\nNext API call at: " + nextApiCall;
        }
        else {
            String deltaTime = new DateHelper()
                    .deltaTime(dp.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);

            String forecastTime = new DateHelper()
                    .formatTime(dp.getTime(), Time.TIME_FORMAT, Time.TIME_ZONE_NEW_YORK, Locale.US);

            if(AnalyzerHelper.compareTo(dp.getIcon(), icon)) {
                forecast = "\nFound: " + dp.getIcon() + "\n\nCurrently: " + currently.getIcon() +
                        "\nat "+ currentTime +
                        "\n\n" + dp.getIcon() + " expected at " + forecastTime +
                        " \n" + deltaTime + ".\n\nNext API call at: " + nextApiCall;
            } else {
                forecast = "\nSearching: " + icon + "\n\nCurrently: " + currently.getIcon() +
                        "\nat "+ currentTime +
                        "\n\n" + dp.getIcon() + " expected at " + forecastTime +
                        " \n" + deltaTime + ".\n\nNext API call at: " + nextApiCall;
            }
        }
        txt_response.setText(forecast);
        Log.d(TAG, ".\n" + forecast);
    }

    private void setupUI() {
        btn_call = (Button) findViewById(R.id.btn_call);
        btn_call.setOnClickListener(this);
        btn_call.setVisibility(View.GONE);
        txt_response = (TextView) findViewById(R.id.txt_response);
        txt_city = (TextView) findViewById(R.id.txt_city);
    }

    public static void setNextApiCallTime(long time) {
        nextApiCallTime = time;
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

