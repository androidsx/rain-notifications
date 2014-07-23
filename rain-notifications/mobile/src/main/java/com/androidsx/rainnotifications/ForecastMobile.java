package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.service.LocationService;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;

/*
 * Activity principal para mostrar los datos de las diferentes llamadas realizadas por el
 * servicio WeatherService a la API forecast.io
 *
 * Se hace especial énfasis en el estado actual del pronóstico del tiempo, y cuándo se prevé
 * el siguiente cambio de tiempo, indicando el tiempo restante, y la siguiente hora a la que
 * se realizará la llamada a forecast.io para actualizar los datos del pronóstico.
 */

public class ForecastMobile extends Activity {

    private static final String TAG = ForecastMobile.class.getSimpleName();

    private TextView locationTextView;
    private TextView nextWeatherTextView;
    private TextView historyTextView;
    private ImageView currentWeatherImageView;
    private ImageView nextWeatherImageView;

    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        setupUI();
    }

    private void setupUI() {
        sharedPrefs = getSharedPreferences(Constants.SharedPref.SHARED_RAIN, 0);

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
    public void callApi(View view) {
        startService(new Intent(this, LocationService.class));
        view.setEnabled(false);
    }

    /** Linked to the button in the XML layout. */
    public void refreshUi(View view) {
        updateUiFromPrefs();
    }

    /**
     * Updates the UI with the information stored in the shared preferences.
     */
    private void updateUiFromPrefs() {
        locationTextView.setText(SharedPrefsHelper.getForecastAddress(sharedPrefs));
        nextWeatherTextView.setText(SharedPrefsHelper.getCurrentForecast(sharedPrefs));
        historyTextView.setText(SharedPrefsHelper.getForecastHistory(sharedPrefs));

        String currentWeatherIcon = SharedPrefsHelper.getCurrentForecastIcon(sharedPrefs);
        if(Constants.ForecastIO.FORECAST_ICON.containsKey(currentWeatherIcon)) {
            currentWeatherImageView.setImageDrawable(getResources().getDrawable(Constants.ForecastIO.FORECAST_ICON.get(currentWeatherIcon)));
        } else {
            currentWeatherImageView.setImageDrawable(getResources().getDrawable(Constants.ForecastIO.FORECAST_ICON.get(Constants.ForecastIO.Icon.UNKNOWN)));
        }

        String nextIcon = SharedPrefsHelper.getNextForecastIcon(sharedPrefs);
        if(Constants.ForecastIO.FORECAST_ICON.containsKey(nextIcon)) {
            nextWeatherImageView.setImageDrawable(getResources().getDrawable(Constants.ForecastIO.FORECAST_ICON.get(nextIcon)));
        } else {
            nextWeatherImageView.setImageDrawable(getResources().getDrawable(Constants.ForecastIO.FORECAST_ICON.get(Constants.ForecastIO.Icon.UNKNOWN)));
        }
    }
}

