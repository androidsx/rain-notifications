package com.androidsx.rainnotifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class WearManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_MESSAGE = "key_message";
    private static final String KEY_CURRENT_ICON = "key_current_image";
    private static final String KEY_FORECAST_ICON = "key_forecast_image";

    private GoogleApiClient mGoogleApiClient;
    private Context context;

    public WearManager(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        this.context = context;
    }

    public void connect() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void sendNotification(String message, Weather currentWeather, Forecast forecast){
        if (mGoogleApiClient.isConnected()) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/forecast");
            // Add data to the request
            putDataMapRequest.getDataMap().putString(KEY_TITLE,
                    context.getResources().getString(R.string.app_name));
            putDataMapRequest.getDataMap().putString(KEY_MESSAGE, message);

            int weatherIcon = Constants.FORECAST_ICONS.containsKey(currentWeather.getType())
                    ? Constants.FORECAST_ICONS.get(currentWeather.getType())
                    : Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);
            int forecastIcon = Constants.FORECAST_ICONS.containsKey(forecast.getForecastedWeather().getType())
                    ? Constants.FORECAST_ICONS.get(forecast.getForecastedWeather().getType())
                    : Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);

            putDataMapRequest.getDataMap().putAsset(KEY_CURRENT_ICON, createAssetFromDrawable(weatherIcon));
            putDataMapRequest.getDataMap().putAsset(KEY_FORECAST_ICON, createAssetFromDrawable(forecastIcon));
            PutDataRequest request = putDataMapRequest.asPutDataRequest();

            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {

                        }
                    });
        }
    }

    private Asset createAssetFromDrawable(int drawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);

        return Asset.createFromBytes(byteStream.toByteArray());
    }

    @Override
    public abstract void onConnected(Bundle bundle);

    @Override
    public abstract void onConnectionSuspended(int i);

    @Override
    public abstract void onConnectionFailed(ConnectionResult connectionResult);
}
