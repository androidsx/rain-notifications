package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.util.Constants.SharedPref;
import com.androidsx.rainnotifications.util.Constants.ForecastIO;

public class ForecastMobile extends Activity {

    private static final String TAG = ForecastMobile.class.getSimpleName();

    private TextView txt_response;
    private TextView txt_city;
    private TextView txt_update;
    private ImageView icon;
    private ImageView update_icon;

    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_mobile);

        setupUI();
    }

    private void setupUI() {
        shared = getSharedPreferences(SharedPref.SHARED_RAIN, 0);

        txt_response = (TextView) findViewById(R.id.txt_response);
        txt_city = (TextView) findViewById(R.id.txt_city);
        txt_update = (TextView) findViewById(R.id.txt_update);
        icon = (ImageView) findViewById(R.id.icon);
        update_icon = (ImageView) findViewById(R.id.update_icon);

        txt_city.setText(shared.getString(SharedPref.ADDRESS, ""));
        txt_update.setText(shared.getString(SharedPref.CURRENTLY, ""));
        txt_response.setText(shared.getString(SharedPref.HISTORY, ""));
        String ic = shared.getString(SharedPref.CURRENTLY_ICON, "");
        icon.setImageDrawable(getResources().getDrawable(getIcon(ic)));
        ic = shared.getString(SharedPref.NEXT_FORECAST_ICON, "");
        update_icon.setImageDrawable(getResources().getDrawable(getIcon(ic)));
    }

    /** Linked to the button in the XML layout. */
    public void callApi(View view) {
        startService(new Intent(this, WeatherService.class));
        view.setEnabled(false);
    }

    /** Linked to the button in the XML layout. */
    public void refresh(View view) {
        String ic = shared.getString(SharedPref.CURRENTLY_ICON, "");
        txt_city.setText(shared.getString(SharedPref.ADDRESS, ""));
        txt_update.setText(shared.getString(SharedPref.CURRENTLY, ""));
        txt_response.setText(shared.getString(SharedPref.HISTORY, ""));
        icon.setImageDrawable(getResources().getDrawable(getIcon(ic)));
        ic = shared.getString(SharedPref.NEXT_FORECAST_ICON, "");
        update_icon.setImageDrawable(getResources().getDrawable(getIcon(ic)));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forecast_mobile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

