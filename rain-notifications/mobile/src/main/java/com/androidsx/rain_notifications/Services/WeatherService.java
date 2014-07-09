package com.androidsx.rain_notifications.Services;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.androidsx.rain_notifications.Models.LocationObservable;
import com.forecast.io.network.responses.INetworkResponse;
import com.forecast.io.network.responses.NetworkResponse;
import com.forecast.io.toolbox.NetworkServiceTask;
import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.androidsx.rain_notifications.R;

public class WeatherService {

    private static final String API_KEY = "f1fd27e70564bd6765bf40b3497cbf4f";
    private static final String TIME_ZONE = "Europe/Madrid";
    private static final String TIME_FORMAT = "HH:mm";

    public static void getWeather(Context context, final String city, Double lat, Double lon, int time) {
        LatLng.Builder builderL = LatLng.newBuilder();
        builderL.setLatitude(lat).setLongitude(lon).setTime(System.currentTimeMillis() / 1000 + time).build();
        LatLng latlng = new LatLng(builderL);

        ForecastService.Builder builderF = ForecastService.Request.newBuilder( API_KEY );
        builderF.setLatLng(latlng).build();
        ForecastService.Request request = new ForecastService.Request(builderF);

        final TextView txt_city = (TextView)((Activity) context).findViewById(R.id.txt_city);
        final TextView txt_response = (TextView)((Activity) context).findViewById(R.id.txt_response);

        txt_city.setText(city);

        new NetworkServiceTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                txt_response.setText("Loading...");
                txt_city.setText("Loading...");
            }

            @Override
            protected void onPostExecute( INetworkResponse network ) {
                if ( network == null || network.getStatus() == NetworkResponse.Status.FAIL ) {
                    txt_response.setText("FORECAST ERROR: " + network.getStatus().toString());
                    return;
                }

                ForecastService.Response response = (ForecastService.Response) network;

                String summary = response.getForecast().getCurrently().getSummary();
                String icon = response.getForecast().getCurrently().getIcon();

                //Show time interval in char sequence
                CharSequence realTime = DateUtils.getRelativeTimeSpanString(
                        response.getForecast().getCurrently().getTime() * 1000,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS);

                SimpleDateFormat sdfDateTime = new SimpleDateFormat(TIME_FORMAT, Locale.US);
                sdfDateTime.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
                String newTime =  sdfDateTime.format(new Date(response.getForecast().getCurrently().getTime() * 1000));

                txt_city.setText(city);
                txt_response.setText(summary + "\n\n" + realTime.toString() + " at " + newTime);
            }

        }.execute( request );
    }
}
