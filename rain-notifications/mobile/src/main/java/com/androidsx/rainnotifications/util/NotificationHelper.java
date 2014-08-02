package com.androidsx.rainnotifications.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.androidsx.rainnotifications.ForecastMobile;
import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;

/*
 * This helper class is for notify the user by notifications if a significant weather change
 * is near to occur.
 */

public class NotificationHelper {

    private static final String TAG = NotificationHelper.class.getSimpleName();

    private NotificationHelper() {
        //No-instantiate
    }

    public static void sendNotification(Context context, int id, int nowIcon, int nextIcon, String notification) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(nextIcon);
        mBuilder.setContentTitle(context.getString(R.string.app_name));
        mBuilder.setContentText(notification);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                nowIcon));
        Intent intent = new Intent(context, ForecastMobile.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        mBuilder.setContentIntent(pi);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id, mBuilder.build());
    }
}
