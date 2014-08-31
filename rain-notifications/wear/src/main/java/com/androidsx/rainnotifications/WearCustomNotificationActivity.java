package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.commonlibrary.Constants;
import com.google.android.gms.wearable.Asset;

public class WearCustomNotificationActivity extends Activity {
    private static final String TAG = WearCustomNotificationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_notification_layout);
        final Intent mIntent = getIntent();

        // TODO: replace by a full Alert
        final String message = mIntent.getStringExtra(Constants.Extras.EXTRA_TEXT);
        final Asset mascot = mIntent.getParcelableExtra(Constants.Extras.EXTRA_MASCOT_ICON);
        Log.i(TAG, "Will create a custom notification \"" + message + "\"");

        // Set the notification text
        final TextView mTextView = (TextView) findViewById(R.id.text);
        mTextView.setText(message);

        // Set the notification icon
        final ImageView mMascotImageView = (ImageView) findViewById(R.id.mascot_icon);
        AssetHelper.loadBitmapFromAsset(WearCustomNotificationActivity.this, mascot, mMascotImageView);
    }
}
