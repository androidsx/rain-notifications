package com.androidsx.rainnotifications;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
                    final String text = dataMapItem.getDataMap().getString(Constants.Keys.KEY_TEXT);
                    Asset assetBackground = dataMapItem.getDataMap().getAsset(Constants.Keys.KEY_BACKGROUND);
                    Asset assetContentIcon = dataMapItem.getDataMap().getAsset(Constants.Keys.KEY_CONTENT_ICON);

                    Intent actionIntent = new Intent(this, ForecastWear.class);
                    actionIntent.putExtra(Constants.Extras.EXTRA_TITLE, title);
                    actionIntent.putExtra(Constants.Extras.EXTRA_TEXT, text);
                    actionIntent.putExtra(Constants.Extras.EXTRA_BACKGROUND, assetBackground);
                    actionIntent.putExtra(Constants.Extras.EXTRA_CONTENT_ICON, assetContentIcon);
                    PendingIntent actionPendingIntent = PendingIntent.getActivity(
                            this,
                            0,
                            actionIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Action action =
                            new NotificationCompat.Action.Builder(R.drawable.ic_launcher,
                                    getString(R.string.app_name), actionPendingIntent)
                                    .build();

                    // Intent for change the standard notification by our custom notification layout
                    Intent notificationIntent = new Intent(this, CustomNotification.class);
                    PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setContentTitle(title)
                                    .setContentText(text)
                                    .setSmallIcon(R.drawable.clear_day)
                                    .extend(new WearableExtender()
                                                    .setHintHideIcon(true)
                                                    .setCustomContentHeight(400)
                                                    .setContentIcon(R.drawable.ic_launcher)
                                                    .setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.custom_notification_background))
                                                    .setDisplayIntent(notificationPendingIntent)
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
