package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.util.LocationHelper;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewWeatherService extends Service {

    private static final String TAG = WeatherService.class.getSimpleName();

    private static final long WEATHER_REPEATING_TIME_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long TEN_MINUTES_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long TWO_HOUR_MILLIS = 2 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long DEFAULT_EXTRA_TIME_MILLIS = 2 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    private final AlertGenerator alertGenerator = new AlertGenerator();

    public SharedPreferences sharedPrefs;
    private PendingIntent weatherAlarmIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPrefs = getSharedPreferences(Constants.SharedPref.SHARED_RAIN, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        weatherAlarmIntent = PendingIntent.getService(this, Constants.AlarmId.WEATHER_ID, intent, 0);

        getLocation();

        return super.onStartCommand(intent, flags, startId);
    }

    private void checkForecast(final double latitude, final double longitude, final String address) {
        if (LocationHelper.rightCoordinates(latitude, longitude)) {

            SharedPrefsHelper.setForecastAddress(address, sharedPrefs.edit());

            new ForecastIoNetworkServiceTask() {

                @Override
                protected void onSuccess(ForecastTable forecastTable) {
                    // TODO: Here is where we should apply our logic
                    Log.d(TAG, "Forecast table: " + forecastTable);

                    Log.i(TAG, "We could generate the following alerts:");
                    final Weather currentWeather = forecastTable.getBaselineWeather();
                    for (Forecast forecast  : forecastTable.getForecasts()) {
                        final Alert alert = alertGenerator.generateAlert(currentWeather, forecast);
                        if (alert.getAlertLevel() == AlertLevel.INFO) {
                            Log.i(TAG, "INFO alert: " + alert.getAlertMessage());
                        }
                    }

                    Bundle mBundle = new Bundle();
                    mBundle.putString(Constants.Extras.EXTRA_ADDRESS, address);
                    mBundle.putDouble(Constants.Extras.EXTRA_LAT, latitude);
                    mBundle.putDouble(Constants.Extras.EXTRA_LON, longitude);

                    if(forecastTable.getForecasts().isEmpty()) {
                        updateWeatherAlarm(System.currentTimeMillis() + DEFAULT_EXTRA_TIME_MILLIS, mBundle);
                        Log.i(TAG, "Next expected forecast: no changes expected in next days.");
                    } else {
                        updateWeatherAlarm(forecastTable.getForecasts().get(0).getTimeFromNow().getEndMillis(), mBundle);
                        Log.i(TAG, "Next expected forecast: " + forecastTable.getForecasts().get(0).toString());
                    }
                    stopSelf();
                }

                @Override
                protected void onFailure() {
                    // TODO: And here is where we do something smart about failures
                    stopSelf();
                }
            }.execute(new ForecastIoRequest(latitude, longitude).getRequest());
        }
    }

    private void updateWeatherAlarm(long expectedHour, Bundle mBundle) {
        if(weatherAlarmIntent != null) {
            weatherAlarmIntent.cancel();
        }
        weatherAlarmIntent = PendingIntent.getService(
                this,
                Constants.AlarmId.WEATHER_ID,
                new Intent(this, WeatherService.class).putExtras(mBundle),
                0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(am != null) {
            am.cancel(weatherAlarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    nextWeatherCallAlarmTime(expectedHour),
                    WEATHER_REPEATING_TIME_MILLIS,
                    weatherAlarmIntent);
        }
        Log.i(TAG, "Next weather alarm at: " + new LocalTime(nextWeatherCallAlarmTime(expectedHour)));
    }

    // That method is for determine the next time that we must call again to WeatherService.
    private long nextWeatherCallAlarmTime(long expectedHour) {
        final long currentTime = System.currentTimeMillis();
        if ((expectedHour - currentTime) < TEN_MINUTES_MILLIS) {
            return expectedHour;
        } else if (expectedHour - currentTime < TWO_HOUR_MILLIS){
            return currentTime + ((expectedHour - currentTime) * 70 / 100);
        } else {
            return currentTime + TWO_HOUR_MILLIS;
        }
    }

    private void getLocation() {
        new UserLocation(this) {
            @Override
            public void obtainedLocation(Location loc) {
                String address = getLocationAddress(NewWeatherService.this, loc.getLatitude(), loc.getLongitude());
                checkForecast(loc.getLatitude(), loc.getLongitude(), address);
            }
        }.getUserLocation();
    }

    private static String getLocationAddress(Context context, double latitude, double longitude) {
        String address = context.getString(R.string.current_name_location);

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            if(addresses.get(0).getAddressLine(0) != null) address = addresses.get(0).getAddressLine(0);
            else if(addresses.get(0).getSubLocality() != null) address = addresses.get(0).getSubLocality();
            else if(addresses.get(0).getLocality() != null) address = addresses.get(0).getLocality();
            else if(addresses.get(0).getCountryName() != null) address = addresses.get(0).getCountryName();
        }

        return address;
    }
}
