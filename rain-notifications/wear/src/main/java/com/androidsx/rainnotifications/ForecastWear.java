package com.androidsx.rainnotifications;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.wearable.Asset;

import java.io.IOException;
import java.io.InputStream;

public class ForecastWear extends Activity {

    private static final String TAG = ForecastWear.class.getSimpleName();
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_CURRENT_ICON = "extra_current_icon";
    public static final String EXTRA_FORECAST_ICON = "extra_forecast_icon";

    private TextView forecastTextView;
    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                forecastTextView = (TextView) stub.findViewById(R.id.forecastTextView);
                iconImageView = (ImageView) stub.findViewById(R.id.iconImageView);
            }
        });

        Bundle mBundle = getIntent().getExtras();
        if(mBundle != null) {
            forecastTextView.setText(mBundle.get(EXTRA_MESSAGE).toString());
            iconImageView.setImageBitmap(getBitmapFromAsset((Asset) mBundle.get(EXTRA_FORECAST_ICON)));
        }
    }

    private Bitmap getBitmapFromAsset(Asset asset) {
        InputStream is = null;
        try {
            is.read(asset.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(is);
    }
}
