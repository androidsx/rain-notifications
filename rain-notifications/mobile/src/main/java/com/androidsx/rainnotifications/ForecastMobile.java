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

    private TextView txt_response;
    private TextView txt_city;
    private TextView txt_update;
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

        txt_response = (TextView) findViewById(R.id.txt_response);
        txt_city = (TextView) findViewById(R.id.txt_city);
        txt_update = (TextView) findViewById(R.id.txt_update);
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
        txt_city.setText(SharedPrefsHelper.getForecastAddress(sharedPrefs));
        txt_update.setText(SharedPrefsHelper.getCurrentForecast(sharedPrefs));
        txt_response.setText(SharedPrefsHelper.getForecastHistory(sharedPrefs));

        final String currentWeatherIcon = SharedPrefsHelper.getCurrentForecastIcon(sharedPrefs);
        currentWeatherImageView.setImageDrawable(getResources().getDrawable(Constants.ForecastIO.FORECAST_ICON.get(currentWeatherIcon)));

        final String nextIcon = SharedPrefsHelper.getNextForecastIcon(sharedPrefs);
        nextWeatherImageView.setImageDrawable(getResources().getDrawable(Constants.ForecastIO.FORECAST_ICON.get(nextIcon)));
    }
}

