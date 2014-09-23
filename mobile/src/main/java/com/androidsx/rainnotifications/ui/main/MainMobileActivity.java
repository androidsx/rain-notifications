package com.androidsx.rainnotifications.ui.main;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.ui.debug.DebugActivity;
import com.androidsx.rainnotifications.util.AnimationHelper;
import com.androidsx.rainnotifications.util.UserLocationFetcher;
import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.ui.welcome.BaseWelcomeActivity;
import com.androidsx.rainnotifications.weatherclientfactory.WeatherClientFactory;
import com.crashlytics.android.Crashlytics;

import org.joda.time.Interval;

import timber.log.Timber;

/**
 * Main activity.
 */
public class MainMobileActivity extends BaseWelcomeActivity {
    private static final int NUM_CLICKS_FOR_DEBUG_SCREEN = 6;
    private static final String FAHRENHEIT = "ºF";
    private static final String CELSIUS = "ºC";

    private AlertGenerator alertGenerator;

    private TextView tempTextView;
    private TextView degreesTextView;
    private TextView locationTextView;
    private TextView cardMessageTextView;
    private LinearLayout loadingContainer;
    private LinearLayout cardContainer;

    /** Image of the mascot that represents the current weather. */
    private ImageView mascotImageView;

    private int numClicksForDebugScreenSoFar = 0;
    private int fTemp = 0;
    private int cTemp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_forecast_mobile);

        alertGenerator = new AlertGenerator(this);
        alertGenerator.init();

        setupUI();

        // FIXME: we do exactly the same in the weather service. grr..
        new UserLocationFetcher(this, new UserLocationFetcher.UserLocationResultListener() {
            @Override
            public void onLocationSuccess(final Location location) {
                WeatherClientFactory.requestForecastForLocation(MainMobileActivity.this, location.getLatitude(), location.getLongitude(), new WeatherClientResponseListener() {
                    @Override
                    public void onForecastSuccess(ForecastTable forecastTable) {
                        final Forecast forecast = forecastTable.getForecasts().isEmpty() ? null : forecastTable.getForecasts().get(0);
                        final Alert alert = alertGenerator.generateAlert(forecastTable.getBaselineWeather(), forecast);
                        final String locationAddress = UserLocationFetcher.getLocationAddress(
                                MainMobileActivity.this,
                                location.getLatitude(),
                                location.getLongitude());
                        final Interval interval = forecast == null ? null : forecast.getTimeFromNow();
                        fTemp = forecastTable.getBaselineWeather().getTemp();
                        cTemp = getCelsiusFromFahrenheit(fTemp);
                        updateUI(String.valueOf(fTemp), locationAddress,
                                alert.getDressedMascot(),
                                alert.getAlertMessage().getNotificationMessage(interval));
                    }

                    @Override
                    public void onForecastFailure(WeatherClientException exception) {
                        Timber.e(exception, "Failed to get the forecast");
                        updateUI("", "",
                                R.drawable.owlie_default,
                                getString(R.string.weather_api_error));
                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationFetcher.UserLocationException exception) {
                Timber.e(exception, "Failed to get the location");
                updateUI("", "",
                        R.drawable.owlie_default,
                        getString(R.string.location_error));
            }
        }).connect();
    }

    private void setupUI() {
        tempTextView = (TextView) findViewById(R.id.temp_text_view);
        degreesTextView = (TextView) findViewById(R.id.degrees_text_view);
        locationTextView = (TextView) findViewById(R.id.location_text_view);
        cardMessageTextView = (TextView) findViewById(R.id.card_message_text_view);
        mascotImageView = (ImageView) findViewById(R.id.mascot_image_view);
        loadingContainer = (LinearLayout) findViewById(R.id.loading_container);
        cardContainer = (LinearLayout) findViewById(R.id.card_layout);

        cardContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);

        mascotImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: This trick should time out rather quickly
                if (++numClicksForDebugScreenSoFar == NUM_CLICKS_FOR_DEBUG_SCREEN) {
                    startActivity(new Intent(MainMobileActivity.this, DebugActivity.class));
                    numClicksForDebugScreenSoFar = 0;
                }
            }
        });

        tempTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_SLAB_REGULAR_URL));
        degreesTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_SLAB_REGULAR_URL));
        locationTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_SLAB_REGULAR_URL));
        cardMessageTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_REGULAR_URL));
    }

    private void updateUI(String temp, String address, int mascot_icon, String message) {
        tempTextView.setText(temp);
        degreesTextView.setText(FAHRENHEIT); // By default
        locationTextView.setText(address);
        cardMessageTextView.setText(message);

        mascotImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), mascot_icon));
        AnimationHelper.applyMascotAnimation(mascotImageView);

        loadingContainer.setVisibility(View.GONE);
        cardContainer.setVisibility(View.VISIBLE);
        AnimationHelper.applyCardAnimation(cardContainer);
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

    public void changeDegreeScale(View v) {
        if(degreesTextView.getText().equals(FAHRENHEIT)) {
            degreesTextView.setText(CELSIUS);
            tempTextView.setText(String.valueOf(cTemp));
        } else {
            degreesTextView.setText(FAHRENHEIT);
            tempTextView.setText(String.valueOf(fTemp));
        }
    }

    private int getCelsiusFromFahrenheit(int fahrenheit) {
        return new Double(((fahrenheit - 32) * 5) / 9).intValue();
    }

    private Typeface getTypeface(String url) {
        return Typeface.createFromAsset(getAssets(), url);
    }
}
