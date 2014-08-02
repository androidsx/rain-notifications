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

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.UserLocation;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.util.LocationHelper;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * This service is responsible to make API calls to forecast.io
 * Once it starts, make an API call to forecast.io with the obtained coordinates.
 *
 * The response is analyzed for determine the next alarm time, and if it's appropriate
 * notify to user the next significant weather change.
 */

public class WeatherService extends Service {

    private static final long WEATHER_REPEATING_TIME_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long TEN_MINUTES_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long ONE_HOUR_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long DEFAULT_EXTRA_TIME_MILLIS = 2 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    private final AlertGenerator alertGenerator = new AlertGenerator();

    public SharedPreferences sharedPrefs; //Now only for debug.
    private PendingIntent weatherAlarmIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
        //Now only for debug.
        sharedPrefs = getSharedPreferences(Constants.SharedPref.SHARED_RAIN, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        weatherAlarmIntent = PendingIntent.getService(this, Constants.AlarmId.WEATHER_ID, intent, 0);

        new UserLocation(this) {
            @Override
            public void obtainedLocation(Location loc) {
                if(loc != null) {
                    checkForecast(loc.getLatitude(), loc.getLongitude());
                } else {
                    // TODO: probably notify to user, that the gps is disabled or not available,
                    // if we try to obtain many times the location.
                }
            }
        }.getUserLocation();

        return super.onStartCommand(intent, flags, startId);
    }

    private void checkForecast(final double latitude, final double longitude) {
        if (LocationHelper.rightCoordinates(latitude, longitude)) {
            //Only for debug.
            SharedPrefsHelper.setForecastAddress(
                    getLocationAddress(WeatherService.this, latitude, longitude),
                    sharedPrefs.edit()
            );
            new ForecastIoNetworkServiceTask() {

                @Override
                protected void onSuccess(ForecastTable forecastTable) {
                    // TODO: Here is where we should apply our logic
                    Timber.d("Forecast table: %s", forecastTable);

                    Timber.i("We could generate the following alerts:");
                    final Weather currentWeather = forecastTable.getBaselineWeather();
                    for (Forecast forecast  : forecastTable.getForecasts()) {
                        final Alert alert = alertGenerator.generateAlert(currentWeather, forecast);
                        if (alert.getAlertLevel() == AlertLevel.INFO) {
                            Timber.i("INFO alert: %s", alert.getAlertMessage());
                            int icon;
                            if(Constants.FORECAST_ICONS.containsKey(currentWeather.getType())) {
                                icon = Constants.FORECAST_ICONS.get(currentWeather.getType());
                            } else {
                                icon = Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);
                            }
                            NotificationHelper.sendNotification(WeatherService.this, 1, icon, alert.getAlertMessage().toString());
                        }
                    }

                    if(forecastTable.getForecasts().isEmpty()) {
                        updateWeatherAlarm(System.currentTimeMillis() + DEFAULT_EXTRA_TIME_MILLIS);
                        Timber.i("Next expected forecast: no changes expected in next days.");
                    } else {
                        updateWeatherAlarm(forecastTable.getForecasts().get(0).getTimeFromNow().getEndMillis());
                        Timber.i("Next expected forecast: %s", forecastTable.getForecasts().get(0).toString());
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

    private void updateWeatherAlarm(long expectedHour) {
        weatherAlarmIntent.cancel();
        weatherAlarmIntent = PendingIntent.getService(
                this,
                Constants.AlarmId.WEATHER_ID,
                new Intent(this, WeatherService.class),
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
        Timber.i("Next weather alarm at: %s", new LocalTime(nextWeatherCallAlarmTime(expectedHour)));
    }

    // That method is for determine the next time that we must call again to WeatherService.
    private long nextWeatherCallAlarmTime(long expectedHour) {
        final long currentTime = System.currentTimeMillis();
        if ((expectedHour - currentTime) < TEN_MINUTES_MILLIS) {
            return expectedHour;
        } else if (expectedHour - currentTime < ONE_HOUR_MILLIS){
            return currentTime + getTimePercentage((expectedHour - currentTime), 70);
        } else {
            return currentTime + ONE_HOUR_MILLIS;
        }
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

    private long getTimePercentage(long time, int percentage) {
        return time * percentage / 100;
    }
}
