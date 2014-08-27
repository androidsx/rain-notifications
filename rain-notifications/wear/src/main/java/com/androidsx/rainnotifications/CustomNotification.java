package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.commonlibrary.Constants;
import com.google.android.gms.wearable.Asset;

public class CustomNotification extends Activity {

    private ImageView mPetImageView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_notification_layout);

        mPetImageView = (ImageView) findViewById(R.id.pet_icon);
        mTextView = (TextView) findViewById(R.id.text);

        Intent mIntent = getIntent();

        if(mIntent != null) {
            String text = mIntent.getStringExtra(Constants.Extras.EXTRA_TEXT);
            if(text != null) {
                mTextView.setText(text);
            }
            Asset assetPetIcon = mIntent.getParcelableExtra(Constants.Extras.EXTRA_PET_ICON);
            if(assetPetIcon != null) {
                AssetHelper.loadBitmapFromAsset(CustomNotification.this, assetPetIcon, mPetImageView);
            }
        }
    }
}
