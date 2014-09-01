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
import android.widget.TextView;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.DebugActivity;
import com.androidsx.rainnotifications.UserLocationFetcher;
import com.androidsx.rainnotifications.UserLocationResultListener;
import com.androidsx.rainnotifications.ForecastChecker;
import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.UserLocation;
import com.androidsx.rainnotifications.UserLocationException;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.ui.welcome.BaseWelcomeActivity;

/**
 * Main activity.
 */
public class MainMobileActivity extends BaseWelcomeActivity {
    private static final int NUM_CLICKS_FOR_DEBUG_SCREEN = 6;

    private TextView locationTextView;
    private TextView cardMessageTextView;

    /** Image of the mascot that represents the current weather. */
    private ImageView mascotImageView;

    private int numClicksForDebugScreenSoFar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        setupUI();

        // FIXME: we do exactly the same in the weather service. grr..
        new UserLocation(this) {
            @Override
            public void onLocationSuccess(final Location location) {
                ForecastChecker.requestForecastForLocation(location.getLatitude(), location.getLongitude(),
                        new ForecastCheckerResultListener() {
                            @Override
                            public void onForecastSuccess(ForecastTable forecastTable) {
                                final Forecast forecast = forecastTable.getForecasts().isEmpty() ? null : forecastTable.getForecasts().get(0);
                                final Alert alert = new AlertGenerator().generateAlert(forecastTable.getBaselineWeather(), forecast);
                                final String locationAddress = UserLocation.getLocationAddress(
                                        MainMobileActivity.this,
                                        location.getLatitude(),
                                        location.getLongitude());
                                updateUI(locationAddress,
                                        alert.getDressedMascot(),
                                        alert.getAlertMessage().getNotificationMessage());
                            }

                            @Override
                            public void onForecastFailure(ForecastCheckerException exception) {
                                throw new IllegalStateException("Failed to get the forecast", exception); // FIXME: show a nice message
                            }
                        });
            }

            @Override
            public void onLocationFailure(UserLocationException exception) {
                throw new IllegalStateException("Failed to get the forecast", exception); // FIXME: show a nice message
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
                    startActivity(new Intent(MainMobileActivity.this, DebugActivity.class));
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


    private Typeface getTypeface(String url) {
        return Typeface.createFromAsset(getAssets(), url);
    }
}
