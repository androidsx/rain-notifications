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
    private ImageView mImageView;

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
                }
            }
        });
    }
}
