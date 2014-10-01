package com.androidsx.rainnotifications.dailyclothes.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Spanned;

import com.androidsx.rainnotifications.dailyclothes.R;

/*
 * Helper to build and display notifications on mobile.
 */
public class NotificationHelper {

    private NotificationHelper() {
        // Non-instantiable
    }

    /**
     * Displays a standard notification, that will show up in both mobile and wear.
     */
    public static void displayStandardNotification(Context context, Class<?> activity, Spanned text, Bitmap largeIcon) {
        final int notificationId = 001;

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
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                ;

        // Build the notification and launch it
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build());
    }
}
