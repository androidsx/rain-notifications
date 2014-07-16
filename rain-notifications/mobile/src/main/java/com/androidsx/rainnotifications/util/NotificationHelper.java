package com.androidsx.rainnotifications.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.androidsx.rainnotifications.ForecastMobile;
import com.androidsx.rainnotifications.R;

public class NotificationHelper {

    private static final String TAG = NotificationHelper.class.getSimpleName();
    private static final int ID = 1;

    public NotificationHelper(Context context, String notification) {
        //Log.d(TAG, "Notification...");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("Rain-Notifications");
        mBuilder.setContentText(notification);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);

        Intent intent = new Intent(context, ForecastMobile.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        mBuilder.setContentIntent(pi);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(ID, mBuilder.build());
    }
}
