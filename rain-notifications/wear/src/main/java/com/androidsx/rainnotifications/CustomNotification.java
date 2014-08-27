package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.commonlibrary.Constants;
import com.google.android.gms.wearable.Asset;

public class CustomNotification extends Activity {

    private ImageView mMascotImageView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_notification_layout);

        mMascotImageView = (ImageView) findViewById(R.id.mascot_icon);
        mTextView = (TextView) findViewById(R.id.text);

        Intent mIntent = getIntent();

        if(mIntent != null) {
            String text = mIntent.getStringExtra(Constants.Extras.EXTRA_TEXT);
            if(text != null) {
                mTextView.setText(text);
            }
            Asset assetMascotIcon = mIntent.getParcelableExtra(Constants.Extras.EXTRA_MASCOT_ICON);
            if(assetMascotIcon != null) {
                AssetHelper.loadBitmapFromAsset(CustomNotification.this, assetMascotIcon, mMascotImageView);
            }
        }
    }
}
