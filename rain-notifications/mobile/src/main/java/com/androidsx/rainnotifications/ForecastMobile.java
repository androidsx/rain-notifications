package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.service.LocationService;
import com.androidsx.rainnotifications.util.Constants.ForecastIO;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;

public class ForecastMobile extends Activity {

    private static final String TAG = ForecastMobile.class.getSimpleName();

    private TextView txt_response;
    private TextView txt_city;
    private TextView txt_update;
    private ImageView currentWeatherImageView;
    private ImageView nextWeatherImageView;

    private SharedPrefsHelper sharedHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        setupUI();
    }

    private void setupUI() {
        sharedHelper = new SharedPrefsHelper(getApplicationContext());

        txt_response = (TextView) findViewById(R.id.txt_response);
        txt_city = (TextView) findViewById(R.id.txt_city);
        txt_update = (TextView) findViewById(R.id.txt_update);
        currentWeatherImageView = (ImageView) findViewById(R.id.currentWeatherImageView);
        nextWeatherImageView = (ImageView) findViewById(R.id.nextWeatherImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        txt_city.setText(sharedHelper.getForecastAddress());
        txt_update.setText(sharedHelper.getNextForecast());
        txt_response.setText(sharedHelper.getForecastHistory());

        final String currentWeatherIcon = sharedHelper.getCurrentForecastIcon();
        currentWeatherImageView.setImageDrawable(getResources().getDrawable(getIcon(currentWeatherIcon)));

        final String nextWeatherIcon = sharedHelper.getNextForecastIcon();
        nextWeatherImageView.setImageDrawable(getResources().getDrawable(getIcon(nextWeatherIcon)));
    }

    /** Linked to the button in the XML layout. */
    public void callApi(View view) {
        startService(new Intent(this, LocationService.class));
        view.setEnabled(false);
    }

    /** Linked to the button in the XML layout. */
    public void refreshUi(View view) {
        txt_city.setText(sharedHelper.getForecastAddress());
        txt_update.setText(sharedHelper.getNextForecast());
        txt_response.setText(sharedHelper.getForecastHistory());

        final String currentWeatherIcon = sharedHelper.getCurrentForecastIcon();
        currentWeatherImageView.setImageDrawable(getResources().getDrawable(getIcon(currentWeatherIcon)));

        final String nextIcon = sharedHelper.getNextForecastIcon();
        nextWeatherImageView.setImageDrawable(getResources().getDrawable(getIcon(nextIcon)));
    }

    private int getIcon(String icon) {
        if(icon.equals(ForecastIO.Icon.RAIN)) {
            return R.drawable.rain;
        } else if(icon.equals(ForecastIO.Icon.CLEAR_DAY)) {
            return R.drawable.clear_day;
        } else if(icon.equals(ForecastIO.Icon.CLEAR_NIGHT)) {
            return R.drawable.clear_night;
        } else if(icon.equals(ForecastIO.Icon.CLOUDY)) {
            return R.drawable.cloudy;
        } else if(icon.equals(ForecastIO.Icon.PARTLY_CLOUDY_DAY)) {
            return R.drawable.partly_cloudy_day;
        } else if(icon.equals(ForecastIO.Icon.PARTLY_CLOUDY_NIGHT)) {
            return R.drawable.partly_cloudy_night;
        } else if(icon.equals(ForecastIO.Icon.SNOW)) {
            return R.drawable.snow;
        } else if(icon.equals(ForecastIO.Icon.THUNDERSTORM)) {
            return R.drawable.thunderstorm;
        } else if(icon.equals(ForecastIO.Icon.HAIL)) {
            return R.drawable.hail;
        } else {
            return R.drawable.unknown;
        }
    }
}

