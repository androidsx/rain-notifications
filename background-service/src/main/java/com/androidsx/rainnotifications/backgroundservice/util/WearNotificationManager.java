package com.androidsx.rainnotifications.backgroundservice.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.androidsx.commonlibrary.Constants;
import com.androidsx.rainnotifications.backgroundservice.R;
import com.androidsx.rainnotifications.backgroundservice.util.NotificationHelper;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.joda.time.Interval;

import java.io.ByteArrayOutputStream;

/**
 * Class for send wear notifications.
 */
public class WearNotificationManager {

    public static void sendWearNotification(final Context context, final Alert alert, final Interval interval) {
        new WearManager(context) {
            @Override
            public void onWearManagerSuccess(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                // FIXME: one day we will need to remove this line to not show NEVERMIND alerts
                final int notificationIconRes = alert.getAlertLevel().equals(AlertLevel.NEVER_MIND) ? R.drawable.owlie_default : alert.getDressedMascot();
                if (getConnectedNodesResult.getNodes() != null) {
                    if (getConnectedNodesResult.getNodes().size() > 0) {
                        sendWearNotification(
                                context,
                                alert.getAlertMessage().getNotificationMessage(interval),
                                notificationIconRes
                        );
                    } else {
                        NotificationHelper.displayStandardNotification(
                                context,
                                new Intent("backgroundservices.intent.action.Launch"),
                                alert.getAlertMessage().getNotificationMessage(interval),
                                BitmapFactory.decodeResource(context.getResources(), notificationIconRes)
                        );
                    }
                } else {
                    NotificationHelper.displayStandardNotification(
                            context,
                            new Intent("backgroundservices.intent.action.Launch"),
                            alert.getAlertMessage().getNotificationMessage(interval),
                            BitmapFactory.decodeResource(context.getResources(), notificationIconRes)
                    );
                }
            }

            @Override
            public void onWearManagerFailure(WearNotificationManagerException exception) {

            }
        }.connect();
    }

    private abstract static class WearManager {

        private GoogleApiClient mGoogleApiClient;

        public WearManager(Context context) {
            this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                                @Override
                                public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                    if (getConnectedNodesResult != null) {
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

        public boolean sendWearNotification(Context context, String text, int mascotIcon) {
            if (isGoogleApiClientConnected()) {
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.WEAR_PATH);
                // Add data to the request
                putDataMapRequest.getDataMap().putString(Constants.Keys.KEY_TEXT, text);
                putDataMapRequest.getDataMap().putAsset(Constants.Keys.KEY_MASCOT_ICON, createAssetFromDrawable(context, mascotIcon));
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

        public abstract void onWearManagerSuccess(NodeApi.GetConnectedNodesResult getConnectedNodesResult);
        public abstract void onWearManagerFailure(WearNotificationManagerException exception);
    }

    public static class WearNotificationManagerException extends Exception {
    }
}
