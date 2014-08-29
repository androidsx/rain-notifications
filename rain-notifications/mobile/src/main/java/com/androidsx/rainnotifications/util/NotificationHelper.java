package com.androidsx.rainnotifications.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.androidsx.rainnotifications.R;

/*
 * This helper class is for notify the user by notifications if a significant weather change
 * is near to occur.
 */

public class NotificationHelper {

    private NotificationHelper() {
        //No-instantiate
    }

    public static void sendNotification(Context context, Class<?> activity, String title, String text, Bitmap largeIcon) {
        final int notificationId = 002;

        // Main intent for the notification (click for mobile, swipe left and click for wear)
        Intent viewIntent = new Intent(context, activity);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, 0);

        // Big style for the notification. It only matters for mobile AFAIK, to show several lines
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(title)
                .bigText(text);

        // Finally configure the notification builder
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher) // Compulsory. Only for the phone AFAIK. TODO: follow guidelines: gray
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(viewPendingIntent)
                        .setStyle(bigTextStyle)
                ;

        // Build the notification and launch it
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build());
    }
}
