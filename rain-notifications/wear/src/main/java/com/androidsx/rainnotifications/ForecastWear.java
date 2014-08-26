package com.androidsx.rainnotifications;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.wearable.view.WatchViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidsx.commonlibrary.Constants;
import com.google.android.gms.wearable.Asset;

public class ForecastWear extends Activity {

    private static final int NOTIFICATION_ID = 100;

    private TextView mTitleView;
    private TextView mTextView;
    private ImageView mImageView;
    private LinearLayout mBackgroundLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Only for test
            Intent notificationIntent = new Intent(this, CustomNotification.class);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.owl_example))
                            .setSmallIcon(R.drawable.cloudy_owl)
                            .extend(new NotificationCompat.WearableExtender()
                                            .setHintHideIcon(true)
                                            .setContentIcon(R.drawable.cloudy_owl)
                                            .setCustomContentHeight(300)
                                            .setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.notification_background_fake))
                                            .setDisplayIntent(notificationPendingIntent)
                            );

            NotificationManagerCompat.from(this)
                    .notify(NOTIFICATION_ID, notificationBuilder.build());
        // Only for test

        setContentView(R.layout.activity_forecast_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTitleView = (TextView) stub.findViewById(R.id.title);
                mTextView = (TextView) stub.findViewById(R.id.text);
                mBackgroundLinearLayout = (LinearLayout) stub.findViewById(R.id.background);
                mImageView = (ImageView) stub.findViewById(R.id.image);

                Intent mIntent = getIntent();
                if(mIntent != null) {
                    String title = mIntent.getStringExtra(Constants.Extras.EXTRA_TITLE);
                    String text = mIntent.getStringExtra(Constants.Extras.EXTRA_TEXT);
                    if(text != null) {
                        mTitleView.setText(title);
                        mTextView.setText(text);
                    }
                    Asset assetBackground = mIntent.getParcelableExtra(Constants.Extras.EXTRA_CONTENT_ICON);
                    if(assetBackground != null) {
                        AssetHelper.loadBitmapFromAsset(ForecastWear.this, assetBackground, mImageView);
                    }
                    mBackgroundLinearLayout.setBackground(getResources().getDrawable(R.drawable.notification_background_fake));
                }
            }
        });
    }
}
