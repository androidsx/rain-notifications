package com.androidsx.rain_notifications;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.forecast.io.network.responses.INetworkResponse;
import com.forecast.io.network.responses.NetworkResponse;
import com.forecast.io.toolbox.NetworkServiceTask;
import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.LatLng;


public class forecast_mobile extends Activity implements View.OnClickListener {

    private static final String API_KEY = "f1fd27e70564bd6765bf40b3497cbf4f";
    private static final Double NEW_YORK_LAT = 40.72228267283148;
    private static final Double NEW_YORK_LON = -73.9434814453125;

    private Button btn_call;
    private TextView txt_response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        setupUI();
        onClick(null);
    }

    private void setupUI() {
        btn_call = (Button) findViewById(R.id.btn_call);
        txt_response = (TextView) findViewById(R.id.txt_response);

        btn_call.setOnClickListener(this);
    }

    private void forecast_calls() {
        LatLng.Builder builderL = LatLng.newBuilder();
        builderL.setLatitude(NEW_YORK_LAT).setLongitude(NEW_YORK_LON).setTime(System.currentTimeMillis()/1000).build();
        LatLng latlng = new LatLng(builderL);

        ForecastService.Builder builderF = ForecastService.Request.newBuilder( API_KEY );
        builderF.setLatLng(latlng).build();
        ForecastService.Request request = new ForecastService.Request(builderF);

        new NetworkServiceTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                txt_response.setText("Loading...");
            }

            @Override
            protected void onPostExecute( INetworkResponse network ) {
                if ( network == null || network.getStatus() == NetworkResponse.Status.FAIL ) {
                    txt_response.setText("HOURLY ERROR: " + network.getStatus().toString());
                    return;
                }

                ForecastService.Response response = (ForecastService.Response) network;
                txt_response.setText("HOURLY OK: " + response.getForecast().getCurrently().getSummary());
            }

        }.execute( request );
    }

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

    @Override
    public void onClick(View view) {
        forecast_calls();
    }
}

