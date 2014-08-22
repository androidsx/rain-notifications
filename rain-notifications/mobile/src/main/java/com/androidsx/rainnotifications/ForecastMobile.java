package com.androidsx.rainnotifications;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.util.ApplicationVersionHelper;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;

import timber.log.Timber;

/**
 * Main activity for show the retrieved and analyzed info.
 * We show the current weather, and the next weather change with its remaining time for occur.
 * Next API call too.
 */

public class ForecastMobile extends BaseWelcomeActivity {
    private TextView locationTextView;
    private TextView nextWeatherTextView;
    private TextView historyTextView;
    private ImageView currentWeatherImageView;
    private ImageView nextWeatherImageView;

    private boolean appUsageIsTracked = false;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        setupUI();
    }

    private void setupUI() {
        sharedPrefs = getSharedPreferences(SharedPrefsHelper.SHARED_RAIN, 0);

        locationTextView = (TextView) findViewById(R.id.locationTextView);
        /*nextWeatherTextView = (TextView) findViewById(R.id.nextWeatherTextView);
        historyTextView = (TextView) findViewById(R.id.historyTextView);
        currentWeatherImageView = (ImageView) findViewById(R.id.currentWeatherImageView);
        nextWeatherImageView = (ImageView) findViewById(R.id.nextWeatherImageView);*/

        Typeface font = Typeface.createFromAsset(getAssets(), "roboto-slab/RobotoSlab-Regular.ttf");
        locationTextView.setTypeface(font);

        if(!appUsageIsTracked) {
            trackAppUsage();
            appUsageIsTracked = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //updateUiFromPrefs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_share:
                onShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Linked to the button in the XML layout. */
    public void startWeatherService(View view) {
        startService(new Intent(this, WeatherService.class));
        view.setEnabled(false);
    }

    /** Linked to the button in the XML layout. */
    public void refreshUi(View view) {
        //updateUiFromPrefs();
    }

    public void onShare() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
        startActivity(Intent.createChooser(intent, "Share the Owl"));
    }

    /**
     * Updates the UI with the information stored in the shared preferences.
     */
    private void updateUiFromPrefs() {
        locationTextView.setText(SharedPrefsHelper.getForecastAddress(sharedPrefs));
        nextWeatherTextView.setText(SharedPrefsHelper.getNextForecast(sharedPrefs));
        historyTextView.setText(((RainApplication) getApplication()).getLogHistory());
        currentWeatherImageView.setImageDrawable(getResources().getDrawable(Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN)));
        nextWeatherImageView.setImageDrawable(getResources().getDrawable(Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN)));
        if(SharedPrefsHelper.getCurrentForecastIcon(sharedPrefs) != 0 && SharedPrefsHelper.getNextForecastIcon(sharedPrefs) != 0) {
            currentWeatherImageView.setImageDrawable(getResources().getDrawable(SharedPrefsHelper.getCurrentForecastIcon(sharedPrefs)));
            nextWeatherImageView.setImageDrawable(getResources().getDrawable(SharedPrefsHelper.getNextForecastIcon(sharedPrefs)));
        }
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
}
