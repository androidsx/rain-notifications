package com.androidsx.rainnotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;

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

    private static final String PATH = "/forecast";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_MESSAGE = "key_message";
    private static final String KEY_CURRENT_ICON = "key_current_image";
    private static final String KEY_FORECAST_ICON = "key_forecast_image";
    private static final int NOTIFICATION_ID = 1;

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
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();

        if (!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                return;
            }
        }

        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (PATH.equals(path)) {
                    // Get the data out of the event
                    DataMapItem dataMapItem =
                            DataMapItem.fromDataItem(event.getDataItem());
                    String title = dataMapItem.getDataMap().getString(KEY_TITLE);
                    String message = dataMapItem.getDataMap().getString(KEY_MESSAGE);
                    Asset currentIcon = dataMapItem.getDataMap().getAsset(KEY_CURRENT_ICON);
                    Asset forecastIcon = dataMapItem.getDataMap().getAsset(KEY_FORECAST_ICON);
                    // Build the intent to display our custom notification
                    Intent notificationIntent =
                            new Intent(this, ForecastWear.class);
                    notificationIntent.putExtra(
                            ForecastWear.EXTRA_TITLE, title);
                    notificationIntent.putExtra(
                            ForecastWear.EXTRA_MESSAGE, message);
                    notificationIntent.putExtra(
                            ForecastWear.EXTRA_CURRENT_ICON, currentIcon);
                    notificationIntent.putExtra(
                            ForecastWear.EXTRA_FORECAST_ICON, forecastIcon);
                    PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                            this,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    // Create the ongoing notification
                    Notification.Builder notificationBuilder =
                            new Notification.Builder(this)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setLargeIcon(BitmapFactory.decodeResource(
                                            getResources(), R.drawable.ic_launcher))
                                    .setOngoing(true)
                                    .extend(new Notification.WearableExtender()
                                            .setDisplayIntent(notificationPendingIntent));

                    // Build the notification and show it
                    NotificationManager notificationManager =
                            (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(
                            NOTIFICATION_ID, notificationBuilder.build());
                } else {

                }
            }
        }
    }
}
