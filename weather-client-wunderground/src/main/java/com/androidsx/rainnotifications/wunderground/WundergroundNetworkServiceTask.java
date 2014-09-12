package com.androidsx.rainnotifications.wunderground;

import android.content.Context;
import android.util.Log;

import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientExecutor;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.WundergroundTableBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Wunderground API
 * info: http://www.wunderground.com/weather/api/d/docs?d=resources/phrase-glossary
 */
public final class WundergroundNetworkServiceTask implements WeatherClientExecutor {
    private static final String TAG = WundergroundNetworkServiceTask.class.getSimpleName();

    private static final String WUNDERGROUND_BASE_URL = "http://api.wunderground.com/api/" + Constants.WUNDERGROUND_API_KEY;
    private static final String[] FEATURES = {
            "conditions", // Current time, http://www.wunderground.com/weather/api/d/docs?d=data/conditions
            "hourly", // Hourly forecast, http://www.wunderground.com/weather/api/d/docs?d=data/hourly
            "astronomy"}; // Sunrise/Sunset time, http://www.wunderground.com/weather/api/d/docs?d=data/astronomy

    @Override
    public void execute(Context context, double latitude, double longitude, final WeatherClientResponseListener responseListener) {
        String url = WUNDERGROUND_BASE_URL;
        for(String f : FEATURES) {
            url += "/" + f;
        }
        url += "/q/" + latitude + "," + longitude + ".json";

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(context, url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.v(TAG, "Raw response from Wunderground:\n" + response.toString(1));
                    final ForecastTable forecastTable = WundergroundTableBuilder.buildFromWunderground(response);
                    if (forecastTable != null) {
                        Log.d(TAG, "Transition table: " + forecastTable);
                        responseListener.onForecastSuccess(forecastTable);
                    } else {
                        responseListener.onForecastFailure(new WeatherClientException(
                                "The forecast table is null for the WUnderground response " + response));
                    }
                } catch (JSONException e) {
                    responseListener.onForecastFailure(new WeatherClientException("Failed to process WUnderground response", e));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                responseListener.onForecastFailure(new WeatherClientException(
                        "Failed to read from WUnderground: " + statusCode, throwable));
            }
        });
    }
}
