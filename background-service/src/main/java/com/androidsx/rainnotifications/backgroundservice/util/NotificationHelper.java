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

    //TODO: Refactor this class with better report integration.

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
        displayStandardNotification(context, intent, text, largeIcon, null, null);
    }

    /**
     * @param largeIcon FIXME: not in use at the moment
     */
    public static void displayStandardNotification(Context context, Intent intent, String text, Bitmap largeIcon, String debugSubject, String debugMessage) {
        final int notificationId = 002;
        final Spanned spannedText = Html.fromHtml(text);

        // Main intent for the notification (click for mobile, swipe left and click for wear)
        PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Big style for the notification. It only matters for mobile AFAIK, to show several lines
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(context.getString(R.string.app_name))
                .bigText(spannedText);

        // Finally configure the notification builder
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_notify_small) // Compulsory. Only for the phone AFAIK. TODO: follow guidelines: gray. TODO: to be provided by client
                        //.setLargeIcon(largeIcon)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(spannedText)
                        .setContentIntent(viewPendingIntent)
                        .setStyle(bigTextStyle)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true);

        if(debugSubject != null && debugMessage != null) {
            notificationBuilder.addAction(getMailForecastReportAction(context, debugSubject, debugMessage));
        }

        // Build the notification and launch it
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build());
    }

    private static NotificationCompat.Action getMailForecastReportAction(Context context, String debugSubject, String debugMessage) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, debugSubject);
        email.putExtra(Intent.EXTRA_TEXT, debugMessage);
        email.setType("message/rfc822");
        return new NotificationCompat.Action(R.drawable.ic_action_new_email, "Send report", PendingIntent.getActivity(context, 0, email, PendingIntent.FLAG_CANCEL_CURRENT));
    }
}
