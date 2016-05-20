package com.androidsx.rainnotifications.wunderground;

import android.content.Context;
import android.util.Log;

import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientDailyResponseListener;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientExecutor;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientHourlyResponseListener;
import com.androidsx.rainnotifications.model.DailyForecastTable;
import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.WundergroundDailyTableBuilder;
import com.androidsx.rainnotifications.model.WundergroundTableBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public final class WundergroundNetworkServiceTask implements WeatherClientExecutor {
    private static final String TAG = WundergroundNetworkServiceTask.class.getSimpleName();

    private static final String WUNDERGROUND_BASE_URL = "http://api.wunderground.com/api/" + Constants.WUNDERGROUND_API_KEY;
    private static final String[] FEATURES_HOURLY = {
            "conditions", // Current time, http://www.wunderground.com/weather/api/d/docs?d=data/conditions
            "hourly", // Hourly forecast, http://www.wunderground.com/weather/api/d/docs?d=data/hourly
            "astronomy"}; // Sunrise/Sunset time, http://www.wunderground.com/weather/api/d/docs?d=data/astronomy

    private static final String[] FEATURES_DAILY = {
            "forecast10day" }; // Daily forecast, http://www.wunderground.com/weather/api/d/docs?d=data/forecast10day


    @Override
    public void executeHourly(Context context, double latitude, double longitude, final WeatherClientHourlyResponseListener responseListener) {
        String url = WUNDERGROUND_BASE_URL;
        for(String f : FEATURES_HOURLY) {
            url += "/" + f;
        }
        url += "/q/" + latitude + "," + longitude + ".json";

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(context, url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.v(TAG, "Raw hourly response from Wunderground:\n" + response.toString(1));
                    final ForecastTable forecastTable = WundergroundTableBuilder.buildFromWunderground(response);
                    if (forecastTable != null) {
                        Log.d(TAG, "ForecastTable: " + forecastTable);
                        Log.d(TAG, "Day: " + new Day(forecastTable));
                        responseListener.onForecastSuccess(forecastTable);
                    } else {
                        responseListener.onForecastFailure(new WeatherClientException(
                                "The forecast table is null for the hourly WUnderground response " + response));
                    }
                } catch (JSONException e) {
                    responseListener.onForecastFailure(new WeatherClientException("Failed to process hourly WUnderground response", e));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                responseListener.onForecastFailure(new WeatherClientException(
                        "Failed to read from hourly WUnderground: " + statusCode, throwable));
            }
        });
    }

    @Override
    public void executeDaily(Context context, double latitude, double longitude, final WeatherClientDailyResponseListener responseListener) {
        String url = WUNDERGROUND_BASE_URL;
        for(String f : FEATURES_DAILY) {
            url += "/" + f;
        }
        url += "/q/" + latitude + "," + longitude + ".json";

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(context, url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.v(TAG, "Raw daily response from Wunderground:\n" + response.toString(1));
                    final DailyForecastTable dailyForecastTable = WundergroundDailyTableBuilder.buildFromWunderground(response);
                    if (dailyForecastTable != null) {
                        Log.d(TAG, "DailyForecastTable: " + dailyForecastTable);
                        responseListener.onForecastSuccess(dailyForecastTable);
                    } else {
                        responseListener.onForecastFailure(new WeatherClientException(
                                "The forecast table is null for the daily WUnderground response " + response));
                    }
                } catch (JSONException e) {
                    responseListener.onForecastFailure(new WeatherClientException("Failed to process daily WUnderground response", e));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                responseListener.onForecastFailure(new WeatherClientException(
                        "Failed to read from daily WUnderground: " + statusCode, throwable));
            }
        });
    }
}
