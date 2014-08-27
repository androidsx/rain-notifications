package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.commonlibrary.Constants;
import com.google.android.gms.wearable.Asset;

public class ForecastWear extends Activity {

    private TextView mTitleView;
    private TextView mTextView;
    private ImageView mPetImageView;
    private ImageView mForecastImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_wear);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTitleView = (TextView) stub.findViewById(R.id.title);
                mTextView = (TextView) stub.findViewById(R.id.text);
                mPetImageView = (ImageView) stub.findViewById(R.id.pet_icon);
                mForecastImageView = (ImageView) stub.findViewById(R.id.forecast_icon);

                Intent mIntent = getIntent();
                if(mIntent != null) {
                    String title = mIntent.getStringExtra(Constants.Extras.EXTRA_TITLE);
                    String text = mIntent.getStringExtra(Constants.Extras.EXTRA_TEXT);
                    if(text != null) {
                        mTitleView.setText(title);
                        mTextView.setText(text);
                    }
                    Asset assetPetIcon = mIntent.getParcelableExtra(Constants.Extras.EXTRA_PET_ICON);
                    Asset assetForecastIcon = mIntent.getParcelableExtra(Constants.Extras.EXTRA_FORECAST_ICON);
                    if(assetPetIcon != null) {
                        AssetHelper.loadBitmapFromAsset(ForecastWear.this, assetPetIcon, mPetImageView);
                    }
                    if(assetForecastIcon != null) {
                        AssetHelper.loadBitmapFromAsset(ForecastWear.this, assetForecastIcon, mForecastImageView);
                    }
                }
            }
        });
    }
}
