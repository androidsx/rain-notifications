package com.androidsx.rainnotifications;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.util.ApplicationVersionHelper;
import com.androidsx.rainnotifications.util.WeatherHelper;

import org.joda.time.Period;

import timber.log.Timber;

/**
 * Main activity.
 * <p>
 * It just shows the owl picture and a sample text so far.
 */
public class ForecastMobile extends BaseWelcomeActivity {
    private static final int NUM_CLICKS_FOR_DEBUG_SCREEN = 1;

    private TextView locationTextView;
    private TextView cardMessageTextView;

    /** Image of the mascot that represents the next weather or, if we don't have it, the current one. */
    private ImageView mascotImageView;

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
        final Intent mIntent = getIntent();
        new UserLocation(this) {
            @Override
            public void onLocationSuccess(final Location location) {
                Intent intent = mIntent;
                if(intent == null) {
                    intent = new Intent(ForecastMobile.this, WeatherService.class);
                }
                ForecastChecker.requestForecastForLocation(ForecastMobile.this, intent, location.getLatitude(), location.getLongitude(),
                        new ForecastCheckerResultListener() {
                    @Override
                    public void onForecastSuccess(ForecastTable forecastTable) {
                        final Weather currentWeather = forecastTable.getBaselineWeather();
                        final int weatherIcon = WeatherHelper.getIconFromWeather(currentWeather);

                        final Forecast forecast;
                        if (forecastTable.getForecasts().isEmpty()) {
                            forecast = null;
                        } else {
                            forecast = forecastTable.getForecasts().get(0);
                        }

                        final Alert alert = new AlertGenerator().generateAlert(currentWeather, forecast);
                        final String locationAddress = UserLocation.getLocationAddress(
                                ForecastMobile.this,
                                location.getLatitude(),
                                location.getLongitude());
                        final String message = alert.getAlertMessage().toString();
                        updateUI(locationAddress, weatherIcon, message);
                    }

                    @Override
                    public void onForecastFailure(ForecastCheckerException exception) {

                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationException exception) {

            }
        }.connect();
    }

    private void setupUI() {
        locationTextView = (TextView) findViewById(R.id.location_text_view);
        cardMessageTextView = (TextView) findViewById(R.id.card_message_text_view);
        mascotImageView = (ImageView) findViewById(R.id.owl_image_view);
        mascotImageView.setOnClickListener(new View.OnClickListener() {
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
        cardMessageTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_REGULAR_URL));
    }

    private void updateUI(String address, int mascot_icon, String message) {
        locationTextView.setText(address);
        mascotImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), mascot_icon));
        cardMessageTextView.setText(message);
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
