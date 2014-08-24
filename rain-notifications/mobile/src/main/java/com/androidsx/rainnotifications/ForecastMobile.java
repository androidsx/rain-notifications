package com.androidsx.rainnotifications;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.util.ApplicationVersionHelper;

import timber.log.Timber;

/**
 * Main activity.
 * <p>
 * It just shows the owl picture and a sample text so far.
 */
public class ForecastMobile extends BaseWelcomeActivity {
    private static final int NUM_CLICKS_FOR_DEBUG_SCREEN = 6;

    private TextView locationTextView;
    private TextView cardMessageTextView;
    private TextView cardTitleTextView;
    private ImageView owlImageView;

    private boolean appUsageIsTracked = false;
    private int numClicksForDebugScreenSoFar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        setupUI();
        if (!appUsageIsTracked) {
            trackAppUsage();
            appUsageIsTracked = true;
        }
    }

    private void setupUI() {
        locationTextView = (TextView) findViewById(R.id.location_text_view);
        cardMessageTextView = (TextView) findViewById(R.id.card_message_text_view);
        cardTitleTextView = (TextView) findViewById(R.id.card_title_text_view);
        owlImageView = (ImageView) findViewById(R.id.owl_image_view);
        owlImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: This trick should time out rather quickly
                if (++numClicksForDebugScreenSoFar == NUM_CLICKS_FOR_DEBUG_SCREEN) {
                    startActivity(new Intent(ForecastMobile.this, DebugActivity.class));
                    numClicksForDebugScreenSoFar = 0;
                }
            }
        });

        locationTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_SLAB_REGULAR_URL));
        locationTextView.setText(getString(R.string.current_name_location));
        cardMessageTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_REGULAR_URL));
        cardMessageTextView.setText(getString(R.string.owl_example));
        cardTitleTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_REGULAR_URL));
        cardTitleTextView.setText(getString(R.string.today));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareApp() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_dialog_title)));
    }

    /**
     * Tracks this usage of the application.
     */
    private void trackAppUsage() {
        final int numUsages = ApplicationVersionHelper.getNumUses(this);
        if (numUsages == 0) {
            Timber.i("New install. Setting the usage count to 0");
        } else {
            Timber.d("Usage number #" + (numUsages + 1));
        }

        ApplicationVersionHelper.saveNewUse(this);
        ApplicationVersionHelper.saveCurrentVersionCode(this);
    }

    private Typeface getTypeface(String url) {
        return Typeface.createFromAsset(getAssets(), url);
    }
}
