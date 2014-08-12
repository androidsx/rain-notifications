package com.androidsx.rainnotifications;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.util.Log;

import com.androidsx.commonlibrary.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class OngoingNotificationListenerService extends WearableListenerService {

    private static final String TAG = OngoingNotificationListenerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 100;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();

        if (!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "Service failed to connect to GoogleApiClient.");
                return;
            }
        }

        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (Constants.WEAR_PATH.equals(path)) {
                    // Get the data out of the event
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    final String title = dataMapItem.getDataMap().getString(Constants.Keys.KEY_TITLE);
                    final String message = dataMapItem.getDataMap().getString(Constants.Keys.KEY_MESSAGE);
                    Asset assetCurrent = dataMapItem.getDataMap().getAsset(Constants.Keys.KEY_CURRENT_ICON);
                    Asset assetForecast = dataMapItem.getDataMap().getAsset(Constants.Keys.KEY_FORECAST_ICON);

                    Intent actionIntent = new Intent(this, ForecastWear.class);
                    actionIntent.putExtra(Constants.Extras.EXTRA_TITLE, title);
                    actionIntent.putExtra(Constants.Extras.EXTRA_MESSAGE, message);
                    actionIntent.putExtra(Constants.Extras.EXTRA_CURRENT_ICON, assetCurrent);
                    actionIntent.putExtra(Constants.Extras.EXTRA_FORECAST_ICON, assetForecast);
                    PendingIntent actionPendingIntent = PendingIntent.getActivity(
                            this,
                            0,
                            actionIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Action action =
                            new NotificationCompat.Action.Builder(R.drawable.rain,
                                    title, actionPendingIntent)
                                    .build();

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setContentTitle(title)
                                    .setContentText(message)
                                    .setLargeIcon(AssetHelper.loadBitmapFromAsset(mGoogleApiClient, assetCurrent))
                                    .setSmallIcon(R.drawable.clear_day)
                                    .extend(new WearableExtender()
                                                    .addAction(action)
                                    );

                    NotificationManagerCompat.from(this)
                            .notify(NOTIFICATION_ID, notificationBuilder.build());
                } else {
                    Log.d(TAG, "Unrecognized path: " + path);
                }
            }
        }
    }
}
