package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.commonlibrary.Constants;
import com.google.android.gms.wearable.Asset;

public class WearNotification extends Activity {

    private TextView message;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wear_notification);

        message = (TextView) findViewById(R.id.messageTextView);
        icon = (ImageView) findViewById(R.id.iconImageView);

        Intent mIntent = getIntent();
        if(mIntent != null) {
            message.setText(mIntent.getStringExtra(Constants.Extras.EXTRA_MESSAGE));
            final Asset asset = mIntent.getParcelableExtra(Constants.Extras.EXTRA_FORECAST_ICON);

            AssetHelper.loadBitmapFromAsset(this, asset, icon);
        }
    }
}
