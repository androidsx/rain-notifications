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

public abstract class WearNotificationManager implements WearNotificationManagerResultListener {

    private GoogleApiClient mGoogleApiClient;

    public WearNotificationManager(Context context) {
        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                if(getConnectedNodesResult != null) {
                                    onWearManagerSuccess(getConnectedNodesResult);
                                } else {
                                    onWearManagerFailure(new WearNotificationManagerException());
                                }
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .build();
    }

    public void connect() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    private boolean isGoogleApiClientConnected() {
        return mGoogleApiClient.isConnected();
    }

    public boolean sendWearNotification(Context context, String title, String text, int mascotIcon, int forecastIcon){
        if (isGoogleApiClientConnected()) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.WEAR_PATH);
            // Add data to the request
            putDataMapRequest.getDataMap().putString(Constants.Keys.KEY_TITLE, title);
            putDataMapRequest.getDataMap().putString(Constants.Keys.KEY_TEXT, text);
            putDataMapRequest.getDataMap().putAsset(Constants.Keys.KEY_MASCOT_ICON, createAssetFromDrawable(context, mascotIcon));
            putDataMapRequest.getDataMap().putAsset(Constants.Keys.KEY_FORECAST_ICON, createAssetFromDrawable(context, forecastIcon));
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

    private Asset createAssetFromDrawable(Context context, int drawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);

        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
