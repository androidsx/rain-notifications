package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;
import com.google.android.gms.wearable.NodeApi;

import timber.log.Timber;

public class DebugActivity extends Activity {
    private TextView locationTextView;
    private TextView nextWeatherTextView;
    private TextView historyTextView;
    private ImageView currentWeatherImageView;
    private ImageView nextWeatherImageView;

    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_layout);

        setupUI();
    }

    private void setupUI() {
        sharedPrefs = getSharedPreferences(SharedPrefsHelper.SHARED_RAIN, 0);

        locationTextView = (TextView) findViewById(R.id.locationTextView);
        nextWeatherTextView = (TextView) findViewById(R.id.nextWeatherTextView);
        historyTextView = (TextView) findViewById(R.id.historyTextView);
        currentWeatherImageView = (ImageView) findViewById(R.id.currentWeatherImageView);
        nextWeatherImageView = (ImageView) findViewById(R.id.nextWeatherImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUiFromPrefs();
    }

    /** Linked to the button in the XML layout. */
    public void startWeatherService(View view) {
        startService(new Intent(this, WeatherService.class));
        view.setEnabled(false);
    }

    /** Linked to the button in the XML layout. */
    public void refreshUi(View view) {
        updateUiFromPrefs();
    }

    /** Linked to the button in the XML layout. */
    public void showNotification(View view) {
        Timber.d("Show a random notification");
        new WearNotificationManager(this) {
            @Override
            public void onWearManagerSuccess(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                if (getConnectedNodesResult.getNodes() != null) {
                    if (getConnectedNodesResult.getNodes().size() > 0) {
                        sendWearNotification(
                                getString(R.string.notif_title),
                                getString(R.string.notif_long_text_fake),
                                R.drawable.notification_background_fake,
                                R.drawable.owl_sunny_fake
                        );
                    } else {
                        NotificationHelper.sendNotification(
                                DebugActivity.this,
                                ForecastMobile.class,
                                getString(R.string.notif_title),
                                getString(R.string.notif_long_text_fake),
                                BitmapFactory.decodeResource(getResources(), R.drawable.owl_sunny_fake)
                        );
                    }
                } else {
                    NotificationHelper.sendNotification(
                            DebugActivity.this,
                            ForecastMobile.class,
                            getString(R.string.notif_title),
                            getString(R.string.notif_long_text_fake),
                            BitmapFactory.decodeResource(getResources(), R.drawable.owl_sunny_fake)
                    );
                }
            }

            @Override
            public void onWearManagerFailure(WearNotificationManagerException exception) {

            }
        }.connect();
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
        if (SharedPrefsHelper.getCurrentForecastIcon(sharedPrefs) != 0 && SharedPrefsHelper.getNextForecastIcon(sharedPrefs) != 0) {
            currentWeatherImageView.setImageDrawable(getResources().getDrawable(SharedPrefsHelper.getCurrentForecastIcon(sharedPrefs)));
            nextWeatherImageView.setImageDrawable(getResources().getDrawable(SharedPrefsHelper.getNextForecastIcon(sharedPrefs)));
        }
    }

}
