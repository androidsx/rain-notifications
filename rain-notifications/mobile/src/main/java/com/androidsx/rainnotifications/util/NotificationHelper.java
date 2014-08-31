package com.androidsx.rainnotifications.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.WearNotificationManager;
import com.androidsx.rainnotifications.WearNotificationManagerException;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.ui.main.MainMobileActivity;
import com.google.android.gms.wearable.NodeApi;

/*
 * Helper to build and display notifications on mobile and wear.
 */
public class NotificationHelper {

    private NotificationHelper() {
        // Non-instantiable
    }

    /**
     * If wear is connected, only to wear. Otherwise, standard one.
     */
    public static void displayCustomNotification(final Context context, final Alert alert) {
        new WearNotificationManager(context) {
            @Override
            public void onWearManagerSuccess(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                if (getConnectedNodesResult.getNodes() != null) {
                    if (getConnectedNodesResult.getNodes().size() > 0) {
                        sendWearNotification(
                                context,
                                alert.getAlertMessage().getNotificationMessage(),
                                alert.getDressedMascot()
                        );
                    } else {
                        NotificationHelper.displayStandardNotification(
                                context,
                                MainMobileActivity.class,
                                alert.getAlertMessage().getNotificationMessage(),
                                BitmapFactory.decodeResource(context.getResources(), alert.getDressedMascot())
                        );
                    }
                } else {
                    NotificationHelper.displayStandardNotification(
                            context,
                            MainMobileActivity.class,
                            alert.getAlertMessage().getNotificationMessage(),
                            BitmapFactory.decodeResource(context.getResources(), alert.getDressedMascot())
                    );
                }
            }

            @Override
            public void onWearManagerFailure(WearNotificationManagerException exception) {
                // FIXME: show the notification in the mobile?
            }
        }.connect();
    }

    /**
     * Displays a standard notification, that will show up in both mobile and wear.
     */
    public static void displayStandardNotification(Context context, Class<?> activity, String text, Bitmap largeIcon) {
        final int notificationId = 002;

        // Main intent for the notification (click for mobile, swipe left and click for wear)
        Intent viewIntent = new Intent(context, activity);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, 0);

        // Big style for the notification. It only matters for mobile AFAIK, to show several lines
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(context.getString(R.string.app_name))
                .bigText(text);

        // Finally configure the notification builder
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher) // Compulsory. Only for the phone AFAIK. TODO: follow guidelines: gray
                        .setLargeIcon(largeIcon)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(text)
                        .setContentIntent(viewPendingIntent)
                        .setStyle(bigTextStyle)
                ;

        // Build the notification and launch it
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build());
    }
}
