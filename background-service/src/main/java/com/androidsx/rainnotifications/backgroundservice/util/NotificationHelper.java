package com.androidsx.rainnotifications.backgroundservice.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.androidsx.commonlibrary.CommonConstants;
import com.androidsx.rainnotifications.backgroundservice.R;
import com.androidsx.rainnotifications.model.Alert;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;

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

    /**
     * @param largeIcon FIXME: not in use at the moment
     */
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
                        .setSmallIcon(R.drawable.ic_stat_notify_small) // Compulsory. Only for the phone AFAIK. TODO: follow guidelines: gray. TODO: to be provided by client
                        //.setLargeIcon(largeIcon)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(text)
                        .setContentIntent(viewPendingIntent)
                        .setStyle(bigTextStyle)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true)
                ;

        if(CommonConstants.ENV.equals(CommonConstants.Env.DEV)) {
            notificationBuilder.addAction(getMailForecastReportAction(context));
        }

        // Build the notification and launch it
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build());
    }

    private static NotificationCompat.Action getMailForecastReportAction(Context context) {
        Log.d("TMP", "getMailForecastReportAction"); //TODO: Remove
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, "Forecast " + DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").print(new DateTime()));
        email.putExtra(Intent.EXTRA_TEXT, "message");
        email.setType("message/rfc822");
        return new NotificationCompat.Action(R.drawable.ic_action_new_email, "Send report", PendingIntent.getActivity(context, 0, email, 0));
    }
}
