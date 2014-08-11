package com.androidsx.rainnotifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import java.io.ByteArrayOutputStream;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import com.androidsx.commonlibrary.Constants;

public abstract class WearManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient.isConnected();
    }

    public void sendNotification(String message, int currentWeatherIcon, int forecastIcon){
        if (mGoogleApiClient.isConnected()) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.WEAR_PATH);
            // Add data to the request
            putDataMapRequest.getDataMap().putString(Constants.Keys.KEY_TITLE, context.getResources().getString(R.string.app_name));
            putDataMapRequest.getDataMap().putString(Constants.Keys.KEY_MESSAGE, message);
            putDataMapRequest.getDataMap().putAsset(Constants.Keys.KEY_CURRENT_ICON, createAssetFromDrawable(currentWeatherIcon));
            putDataMapRequest.getDataMap().putAsset(Constants.Keys.KEY_FORECAST_ICON, createAssetFromDrawable(forecastIcon));
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
