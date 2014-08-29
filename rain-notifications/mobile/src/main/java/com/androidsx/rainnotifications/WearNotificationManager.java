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
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import com.androidsx.commonlibrary.Constants;

public abstract class WearNotificationManager
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<NodeApi.GetConnectedNodesResult>, WearNotificationManagerResultListener {

    private GoogleApiClient mGoogleApiClient;
    private Context context;

    public WearNotificationManager(Context context) {
        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
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

    private boolean isGoogleApiClientConnected() {
        return mGoogleApiClient.isConnected();
    }

    public void getConnectedNodes() {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(this);
    }

    @Override
    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
        if(getConnectedNodesResult != null) {
            onWearManagerSuccess(getConnectedNodesResult);
        } else {
            onWearManagerFailure(new WearNotificationManagerException());
        }
    }

    public boolean sendWearNotification(String title, String text, int mascotIcon, int forecastIcon){
        if (isGoogleApiClientConnected()) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.WEAR_PATH);
            // Add data to the request
            putDataMapRequest.getDataMap().putString(Constants.Keys.KEY_TITLE, title);
            putDataMapRequest.getDataMap().putString(Constants.Keys.KEY_TEXT, text);
            putDataMapRequest.getDataMap().putAsset(Constants.Keys.KEY_MASCOT_ICON, createAssetFromDrawable(mascotIcon));
            putDataMapRequest.getDataMap().putAsset(Constants.Keys.KEY_FORECAST_ICON, createAssetFromDrawable(forecastIcon));
            putDataMapRequest.getDataMap().putLong(Constants.Keys.KEY_TIMESTAMP, System.currentTimeMillis());
            PutDataRequest request = putDataMapRequest.asPutDataRequest();

            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {

                        }
                    });
            return true;
        } else {
            return false;
        }
    }

    private Asset createAssetFromDrawable(int drawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);

        return Asset.createFromBytes(byteStream.toByteArray());
    }

    @Override
    public void onConnected(Bundle bundle) {
        getConnectedNodes();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
