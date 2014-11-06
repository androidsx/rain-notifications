package com.androidsx.rainnotifications.ui.debug;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.androidsx.rainnotifications.R;

public class NotificationsTestActivity extends Activity {

    private String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur convallis posuere tortor in commodo. Aenean mollis quam eu tincidunt aliquam. Morbi molestie purus nunc, ut bibendum velit commodo eget. Phasellus non tincidunt dolor. In eget tortor enim. Vestibulum maximus nisl non ultricies eleifend. Fusce iaculis libero et eros suscipit dapibus.";
    private int smallIcon = R.drawable.ic_launcher;
    private Bitmap bigIcon;
    private PendingIntent notificationIntent;
    private NotificationCompat.Action mailAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_test);

        bigIcon = BitmapFactory.decodeResource(getResources(), R.drawable.owlie_clear_night_01);
        notificationIntent = PendingIntent.getActivity(this, 0, new Intent(this, NotificationsTestActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, "Mail Subject");
        email.putExtra(Intent.EXTRA_TEXT, "Mail message");
        email.setType("message/rfc822");
        mailAction =  new NotificationCompat.Action(com.androidsx.rainnotifications.backgroundservice.R.drawable.ic_action_new_email,
                "Send report", PendingIntent.getActivity(this, 0, email, PendingIntent.FLAG_CANCEL_CURRENT));


    }

    public void testAll (View v) {
        testSimpleOnlySmallIcon(null);
        testSimpleBothIcons(null);
        testSimpleWithAction(null);
        testBigTextStyle(null);
        testBigTextStyleWithAction(null);
        testCustomView(null);
        testCustomViewWithAction(null);
        testCustomViewWithActionAndBigText(null);
        testBothContentView(null);
    }

    public void testSimpleOnlySmallIcon (View v) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                                                        .setSmallIcon(smallIcon)
                                                        .setContentTitle("Test Simple, only small icon")
                                                        .setContentText(loremIpsum)
                                                        .setContentIntent(notificationIntent)
                                                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                        .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(100, notificationBuilder.build());
    }

    public void testSimpleBothIcons (View v) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setLargeIcon(bigIcon)
                .setContentTitle("Test Simple, both icons")
                .setContentText(loremIpsum)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(101, notificationBuilder.build());
    }

    public void testSimpleWithAction (View v) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setLargeIcon(bigIcon)
                .setContentTitle("Test Simple with action")
                .setContentText(loremIpsum)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .addAction(mailAction);

        NotificationManagerCompat.from(this).notify(102, notificationBuilder.build());
    }

    public void testBigTextStyle (View v) {

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle("Big Title")
                .bigText("(Big) " + loremIpsum)
                .setSummaryText("Summary text");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setLargeIcon(bigIcon)
                .setContentTitle("Test big text style")
                .setContentText(loremIpsum)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setStyle(bigTextStyle);

        NotificationManagerCompat.from(this).notify(103, notificationBuilder.build());
    }

    public void testBigTextStyleWithAction (View v) {

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle("Big Title")
                .bigText("(Big) " + loremIpsum)
                .setSummaryText("Summary text");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setLargeIcon(bigIcon)
                .setContentTitle("Test big text action")
                .setContentText(loremIpsum)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setStyle(bigTextStyle)
                .addAction(mailAction);

        NotificationManagerCompat.from(this).notify(104, notificationBuilder.build());
    }

    public void testCustomView (View v) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.test_custom_notification);
        remoteViews.setImageViewResource(R.id.imagenotileft,R.drawable.owlie_clear_night_01);
        remoteViews.setImageViewResource(R.id.imagenotiright,R.drawable.ic_launcher);
        remoteViews.setTextViewText(R.id.title, "CustomView Title");
        remoteViews.setTextViewText(R.id.text, loremIpsum);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContent(remoteViews);

        NotificationManagerCompat.from(this).notify(105, notificationBuilder.build());
    }

    public void testCustomViewWithAction (View v) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.test_custom_notification);
        remoteViews.setImageViewResource(R.id.imagenotileft,R.drawable.owlie_clear_night_01);
        remoteViews.setImageViewResource(R.id.imagenotiright,R.drawable.ic_launcher);
        remoteViews.setTextViewText(R.id.title, "CustomViewAction Title");
        remoteViews.setTextViewText(R.id.text, loremIpsum);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContent(remoteViews)
                .addAction(mailAction);

        NotificationManagerCompat.from(this).notify(106, notificationBuilder.build());
    }

    public void testCustomViewWithActionAndBigText (View v) {

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle("Big Title")
                .bigText("(Big) " + loremIpsum)
                .setSummaryText("Summary text");

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.test_custom_notification);
        remoteViews.setImageViewResource(R.id.imagenotileft,R.drawable.owlie_clear_night_01);
        remoteViews.setImageViewResource(R.id.imagenotiright,R.drawable.ic_launcher);
        remoteViews.setTextViewText(R.id.title, "CustomViewActionBig Title");
        remoteViews.setTextViewText(R.id.text, loremIpsum);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContent(remoteViews)
                .setStyle(bigTextStyle)
                .addAction(mailAction);

        NotificationManagerCompat.from(this).notify(107, notificationBuilder.build());
    }

    public void testBigContentView (View v) {

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.test_custom_notification);
        remoteViews.setImageViewResource(R.id.imagenotileft,R.drawable.owlie_clear_night_01);
        remoteViews.setImageViewResource(R.id.imagenotiright,R.drawable.ic_launcher);
        remoteViews.setTextViewText(R.id.title, "BigContentView Title");
        remoteViews.setTextViewText(R.id.text, loremIpsum);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setLargeIcon(bigIcon)
                .setContentTitle("Test BigContentView")
                .setContentText(loremIpsum)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        notification.bigContentView = remoteViews; // Esto así no permite añadir actions

        NotificationManagerCompat.from(this).notify(108, notification);
    }

    public void testBothContentView (View v) {

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.test_custom_notification);
        remoteViews.setImageViewResource(R.id.imagenotileft,R.drawable.owlie_clear_night_01);
        remoteViews.setImageViewResource(R.id.imagenotiright,R.drawable.ic_launcher);
        remoteViews.setTextViewText(R.id.title, "BothContentView Title");
        remoteViews.setTextViewText(R.id.text, loremIpsum);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContent(remoteViews)
                //.setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(mailAction);

        Notification notification = notificationBuilder.build();

        notification.bigContentView = remoteViews; // Esto así no permite añadir actions

        NotificationManagerCompat.from(this).notify(109, notification);
    }
}
