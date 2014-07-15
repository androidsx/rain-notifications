package com.androidsx.rainnotifications.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

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

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(ID, mBuilder.build());
    }
}
