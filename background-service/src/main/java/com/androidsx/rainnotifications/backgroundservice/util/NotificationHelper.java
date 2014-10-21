package com.androidsx.rainnotifications.backgroundservice.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.text.Spanned;

import com.androidsx.rainnotifications.backgroundservice.R;
import com.androidsx.rainnotifications.model.Alert;

import org.joda.time.Interval;

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
    public static void displayWearNotification(final Context context, final Alert alert, final Interval interval) {
        WearNotificationManager.sendWearNotification(context, alert, interval);
    }

    /**
     * Displays a standard notification, that will show up in both mobile and wear.
     */
    public static void displayStandardNotification(Context context, Intent intent, String text, Bitmap largeIcon) {
        displayStandardNotification(context, intent, Html.fromHtml(text), largeIcon);
    }

    public static void displayStandardNotification(Context context, Intent intent, Spanned text, Bitmap largeIcon) {
        final int notificationId = 002;

        // Main intent for the notification (click for mobile, swipe left and click for wear)
        Intent viewIntent = intent;
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, 0);

        // Big style for the notification. It only matters for mobile AFAIK, to show several lines
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(context.getString(R.string.app_name))
                .bigText(text);

        // Finally configure the notification builder
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.ic_stat_notify_small) // Compulsory. Only for the phone AFAIK. TODO: follow guidelines: gray. TODO: to be provided by client
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(text)
                        .setContentIntent(viewPendingIntent)
                        .setStyle(bigTextStyle)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true)
                ;

        // Build the notification and launch it
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build());
    }
}
