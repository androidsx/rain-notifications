package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.commonlibrary.Constants;
import com.google.android.gms.wearable.Asset;

public class CustomNotification extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_notification_layout);
        final Intent mIntent = getIntent();

        // Set the notification text
        final TextView mTextView = (TextView) findViewById(R.id.text);
        mTextView.setText(mIntent.getStringExtra(Constants.Extras.EXTRA_TEXT));

        // Set the notification icon
        final ImageView mMascotImageView = (ImageView) findViewById(R.id.mascot_icon);
        final Asset assetMascotIcon = mIntent.getParcelableExtra(Constants.Extras.EXTRA_MASCOT_ICON);
        AssetHelper.loadBitmapFromAsset(CustomNotification.this, assetMascotIcon, mMascotImageView);
    }
}
