package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.commonlibrary.Constants;
import com.google.android.gms.wearable.Asset;

public class ForecastWear extends Activity {

    private TextView mTextView;
    private ImageView mMascotImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_wear);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mMascotImageView = (ImageView) stub.findViewById(R.id.mascot_icon);

                Intent mIntent = getIntent();
                if (mIntent != null) {
                    String text = mIntent.getStringExtra(Constants.Extras.EXTRA_TEXT);
                    if (text != null) {
                        mTextView.setText(text);
                    }
                    Asset assetMascotIcon = mIntent.getParcelableExtra(Constants.Extras.EXTRA_MASCOT_ICON);
                    if (assetMascotIcon != null) {
                        AssetHelper.loadBitmapFromAsset(ForecastWear.this, assetMascotIcon, mMascotImageView);
                    }
                }
            }
        });
    }
}
