package com.androidsx.rainnotifications.wunderground;

import android.content.Context;

import com.androidsx.rainnotifications.forecastapislibrary.ForecastApis;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.ForecastTableBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public abstract class WundergroundNetworkServiceTask implements ForecastApis{

    private static final String WUNDERGROUND_BASE_URL = "http://api.wunderground.com/api/" + Constants.API_KEY;

    public void execute(Context context, double latitude, double longitude, List<String> features){
        String url = WUNDERGROUND_BASE_URL;
        for(String f : features) {
            url += "/" + f;
        }
        url += "/q/" + latitude + "," + longitude + ".json";

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(context, url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    final ForecastTable forecastTable = ForecastTableBuilder.buildFromForecastIo(response);
                    if (forecastTable != null) {
                        onForecastSuccess(ForecastTableBuilder.buildFromForecastIo(response));
                    } else {
                        onForecastFailure();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onForecastFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onForecastFailure();
            }
        });
    }

}
