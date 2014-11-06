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

import com.androidsx.commonlibrary.CommonConstants;
import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.alert.DayTemplateGenerator;
import com.androidsx.rainnotifications.backgroundservice.util.UserLocationFetcher;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.model.DayTemplateLoaderFactory;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.ui.debug.DebugActivity;
import com.androidsx.rainnotifications.ui.debug.NotificationsTestActivity;
import com.androidsx.rainnotifications.ui.welcome.BaseWelcomeActivity;
import com.androidsx.rainnotifications.util.AnimationHelper;
import com.androidsx.rainnotifications.weatherclientfactory.WeatherClientFactory;
import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

/**
 * Main activity.
 */
public class MainMobileActivity extends BaseWelcomeActivity {
    private static final int NUM_CLICKS_FOR_DEBUG_SCREEN = 6;

    private AlertGenerator alertGenerator;
    private DayTemplateGenerator dayTemplateGenerator;

    private TextView locationTextView;
    private TextView cardMessageTextView;
    private LinearLayout loadingContainer;
    private LinearLayout cardContainer;

    /** Image of the mascot that represents the current weather. */
    private ImageView mascotImageView;

    private int numClicksForDebugScreenSoFar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Remove this
        startActivity(new Intent(MainMobileActivity.this, NotificationsTestActivity.class));
        finish();


        Crashlytics.start(this);
        setContentView(R.layout.activity_forecast_mobile);

        alertGenerator = new AlertGenerator(this);
        dayTemplateGenerator = new DayTemplateGenerator(DayTemplateLoaderFactory.getDayTemplateLoader(this));
        alertGenerator.init();

        setupUI();

        // FIXME: we do exactly the same in the weather service. grr..
        UserLocationFetcher.getUserLocation(this, new UserLocationFetcher.UserLocationResultListener() {
            @Override
            public void onLocationSuccess(final Location location) {
                WeatherClientFactory.requestForecastForLocation(MainMobileActivity.this, location.getLatitude(), location.getLongitude(), new WeatherClientResponseListener() {
                    @Override
                    public void onForecastSuccess(ForecastTable forecastTable) {
                        final String locationAddress = UserLocationFetcher.getLocationAddress(
                                MainMobileActivity.this,
                                location.getLatitude(),
                                location.getLongitude());

                        updateUI(locationAddress,
                                R.drawable.owlie_default,
                                dayTemplateGenerator.generateMessage(MainMobileActivity.this, forecastTable, getString(R.string.default_day_message)));
                    }

                    @Override
                    public void onForecastFailure(WeatherClientException exception) {
                        Timber.e(exception, "Failed to get the forecast");
                        updateUI("",
                                R.drawable.owlie_default,
                                getString(R.string.weather_api_error));
                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationFetcher.UserLocationException exception) {
                Timber.e(exception, "Failed to get the location");
                updateUI("",
                        R.drawable.owlie_default,
                        getString(R.string.location_error));
            }
        });
    }

    private void setupUI() {
        locationTextView = (TextView) findViewById(R.id.location_text_view);
        cardMessageTextView = (TextView) findViewById(R.id.card_message_text_view);
        mascotImageView = (ImageView) findViewById(R.id.mascot_image_view);
        loadingContainer = (LinearLayout) findViewById(R.id.loading_container);
        cardContainer = (LinearLayout) findViewById(R.id.card_layout);

        cardContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);

        if(CommonConstants.ENV.equals(CommonConstants.Env.DEV)) {
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
        }

        locationTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_SLAB_REGULAR_URL));
        cardMessageTextView.setTypeface(getTypeface(Constants.Assets.ROBOTO_REGULAR_URL));
    }

    private void updateUI(String address, int mascot_icon, String message) {
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


    private Typeface getTypeface(String url) {
        return Typeface.createFromAsset(getAssets(), url);
    }
}
