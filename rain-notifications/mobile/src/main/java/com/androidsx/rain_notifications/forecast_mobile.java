package com.androidsx.rain_notifications;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.forecast.io.network.responses.INetworkResponse;
import com.forecast.io.network.responses.NetworkResponse;
import com.forecast.io.toolbox.NetworkServiceTask;
import com.forecast.io.v1.network.responses.InterestingStormsResponse;
import com.forecast.io.v1.network.responses.MultiplePointsTimesResponse;
import com.forecast.io.v1.network.services.HourlyForecastService;
import com.forecast.io.v1.network.services.InterestingStormsService;
import com.forecast.io.v1.network.services.MultiplePointsService;
import com.forecast.io.v1.transfer.LatLng;
import com.forecast.io.v2.network.services.ForecastService;


public class forecast_mobile extends Activity implements View.OnClickListener {

    private static final String API_KEY = "2358ea11167b29a20c2fb02d634b9d3f";
    private Button btn_call;

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

    private void forecast_call() {
        HourlyForecastService.Request request = HourlyForecastService.Request.newBuilder( API_KEY )
                .setForecastType( HourlyForecastService.ForecastType.FORECAST )
                .setLatitude( 37.422006 )
                .setLongitude(-122.084095)
                .build();

        new NetworkServiceTask() {

            @Override
            protected void onPostExecute( INetworkResponse network ) {
                if ( network == null || network.getStatus() == NetworkResponse.Status.FAIL ) {
                    Toast.makeText(forecast_mobile.this, "HOURLY ERROR", Toast.LENGTH_SHORT).show();

                    return;
                }

                HourlyForecastService.Response response = (HourlyForecastService.Response) network;

                Toast.makeText( forecast_mobile.this, response.getSkyResponse().getCurrentSummary(), Toast.LENGTH_SHORT ).show();
            }

        }.execute( request );

        MultiplePointsService.Request multiple = MultiplePointsService.Request.newBuilder( API_KEY )
                .setPoint( LatLng.newBuilder()
                        .setLatitude(37.422006)
                        .setLongitude(-122.084095)
                        .setTime(1364956418))
                .setPoint( LatLng.newBuilder()
                        .setLatitude( 37.422006 )
                        .setLongitude( -122.084095 )
                        .setTime( 1364956418 ) )
                .build();

        new NetworkServiceTask() {

            @Override
            protected void onPostExecute( INetworkResponse network ) {
                if ( network == null || network.getStatus() == NetworkResponse.Status.FAIL ) {
                    Toast.makeText( forecast_mobile.this, "MULTI POINT ERROR", Toast.LENGTH_SHORT ).show();

                    return;
                }

                MultiplePointsService.Response response = (MultiplePointsService.Response) network;

                MultiplePointsTimesResponse points = response.getMultiplePointsTimes();

                Toast.makeText( forecast_mobile.this, points.getSkyPrecipitation() != null ?
                        points.getSkyPrecipitation().get( 0 ).getType() : "NO MULTIPLE POINTS AND TIMES", Toast.LENGTH_SHORT ).show();
            }

        }.execute( multiple );

        new NetworkServiceTask() {

            @Override
            protected void onPostExecute( INetworkResponse network ) {
                if ( network == null || network.getStatus() == NetworkResponse.Status.FAIL ) {
                    Toast.makeText( forecast_mobile.this, "INTERESTING STORMS ERROR", Toast.LENGTH_SHORT ).show();

                    return;
                }

                InterestingStormsService.Response response = (InterestingStormsService.Response) network;

                InterestingStormsResponse storms = response.getInterestingStorms();

                Toast.makeText( forecast_mobile.this, storms.getInterestingStorms() != null ?
                        storms.getInterestingStorms().get( 0 ).getCity() : "NO INTERESTING STORMS", Toast.LENGTH_SHORT ).show();
            }

        }.execute(InterestingStormsService.Request.newBuilder(API_KEY).build());

        new NetworkServiceTask() {

            @Override
            protected void onPostExecute( INetworkResponse network ) {
                if ( network == null || network.getStatus() == NetworkResponse.Status.FAIL ) {
                    Toast.makeText( forecast_mobile.this, "FORECAST ERROR", Toast.LENGTH_SHORT ).show();

                    return;
                }

                ForecastService.Response response = ( ForecastService.Response ) network;

                Toast.makeText( forecast_mobile.this, response.getForecast() != null ?
                        response.getForecast().getCurrently().getSummary() : "FORECAST", Toast.LENGTH_SHORT ).show();
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
        forecast_call();
    }
}

