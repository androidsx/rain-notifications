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
import com.androidsx.rainnotifications.WearManager;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.util.LocationHelper;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;
import com.google.android.gms.common.ConnectionResult;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;
import org.joda.time.Period;

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
    private static final long DEFAULT_EXTRA_TIME_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    private final AlertGenerator alertGenerator = new AlertGenerator();

    public SharedPreferences sharedPrefs; //Now only for debug.
    private PendingIntent weatherAlarmIntent;
    private String log;

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
        log = SharedPrefsHelper.getLogHistory(sharedPrefs);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        weatherAlarmIntent = PendingIntent.getService(this, Constants.AlarmId.WEATHER_ID, intent, 0);

        new UserLocation(this) {
            @Override
            public void obtainedLocation(Location loc) {
                if(loc != null) {
                    log += "\n" + "Time: " + new LocalTime(System.currentTimeMillis());
                    log += "\n" + String.format("Ask forecast.io for the forecast in %s (GPS %f, %f).",
                            getLocationAddress(loc.getLatitude(), loc.getLongitude()),
                            loc.getLatitude(), loc.getLongitude());
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
                    getLocationAddress(latitude, longitude),
                    sharedPrefs.edit()
            );
            new ForecastIoNetworkServiceTask() {

                @Override
                protected void onSuccess(ForecastTable forecastTable) {
                    // TODO: Here is where we should apply our logic
                    final Weather currentWeather = forecastTable.getBaselineWeather();
                    for (Forecast forecast  : forecastTable.getForecasts()) {
                        final Alert alert = alertGenerator.generateAlert(currentWeather, forecast);
                        if (alert.getAlertLevel() == AlertLevel.INFO) {
                            log += "\n" + String.format("INFO alert: %s", alert.getAlertMessage());
                        }
                    }
                    updateWeatherAlarm(
                            currentWeather,
                            forecastTable.getForecasts()
                    );
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

    private void updateWeatherAlarm(Weather currentWeather, List<Forecast> forecasts) {
        long nextAlarmTime;
        if(forecasts.isEmpty()) {
            nextAlarmTime = System.currentTimeMillis() + DEFAULT_EXTRA_TIME_MILLIS;
        } else {
            nextAlarmTime = forecasts.get(0).getTimeFromNow().getEndMillis();
        }
        long nextAlarmTimePeriod = nextWeatherCallAlarmTime(nextAlarmTime) - System.currentTimeMillis();

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
                    nextWeatherCallAlarmTime(nextAlarmTime),
                    WEATHER_REPEATING_TIME_MILLIS,
                    weatherAlarmIntent);
            if(!forecasts.isEmpty()) {
                String phrase = "";
                if(shouldLaunchNotification(nextAlarmTimePeriod)) {
                    String message = NotificationHelper.getOptimumMessage(currentWeather, forecasts.get(0));
                    phrase = String.format("Next transition is %s -> %s in %s: show a notification to the user \"%s\".",
                            currentWeather.getType(),
                            forecasts.get(0).getForecastedWeather().getType(),
                            UiUtil.getDebugOnlyPeriodFormatter().print(
                                    new Period(forecasts.get(0).getTimeFromNow())),
                            message);
                    log += "\n" + phrase;
                    SharedPrefsHelper.setNextForecast(message, sharedPrefs.edit());
                    launchNotification(message, getIconFromWeather(currentWeather), getIconFromWeather(forecasts.get(0).getForecastedWeather()));
                } else {
                    phrase = String.format("Next transition is %s -> %s in %s. Too far for a notification.",
                            currentWeather.getType(),
                            forecasts.get(0).getForecastedWeather().getType(),
                            UiUtil.getDebugOnlyPeriodFormatter().print(
                                    new Period(forecasts.get(0).getTimeFromNow()))
                    );
                    log += "\n" + phrase;
                    SharedPrefsHelper.setNextForecast(phrase, sharedPrefs.edit());
                }
                SharedPrefsHelper.setCurrentForecastIcon(getIconFromWeather(currentWeather), sharedPrefs.edit());
                SharedPrefsHelper.setNextForecastIcon(getIconFromWeather(forecasts.get(0).getForecastedWeather()), sharedPrefs.edit());
                log += "\n" + String.format("Schedule an alarm for %s from now. Bye!",
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(nextAlarmTimePeriod))
                );
            } else {
                log += "\n" + String.format("Schedule an alarm for %s from now, we don't expect changes. Bye!",
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(nextAlarmTimePeriod))
                );
            }
        }
        SharedPrefsHelper.setLogHistory(log, sharedPrefs.edit());
        Timber.i(log);
    }

    /**
     * Method for determine if a notification is launched,
     * depending on the next alarm time period passed as a param.
     *
     * @param nextAlarmTimePeriod
     */
    private boolean shouldLaunchNotification(long nextAlarmTimePeriod) {
        if(nextAlarmTimePeriod < ONE_HOUR_MILLIS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method for send a notification, using a proper message determined by
     * current and forecast weather.
     *
     * @param message
     * @param currentWeatherIcon
     * @param forecastIcon
     */
    private void launchNotification(String message, int currentWeatherIcon, int forecastIcon) {
        NotificationHelper.sendNotification(this, 1, currentWeatherIcon, forecastIcon, message);
    }

    /**
     * Method for send a wear notification, using a proper message determined by
     * current and forecast weather.
     *
     * @param message
     * @param currentWeatherIcon
     * @param forecastIcon
     */
    private void launchWearNotification(final String message, final int currentWeatherIcon, final int forecastIcon) {
        new WearManager(this) {
            @Override
            public void onConnected(Bundle bundle) {
                if(isGoogleApiClientConnected()) {
                    sendNotification(message, currentWeatherIcon, forecastIcon);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }

            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        }.connect();
    }

    /**
     * This method is for determine the next alarm hour,
     * depending on the interval from now to expected hour passed as a param
     *
     * @param expectedHour
     * @return next alarm hour in millis
     */
    private long nextWeatherCallAlarmTime(long expectedHour) {
        final long currentTime = System.currentTimeMillis();
        if ((expectedHour - currentTime) < TEN_MINUTES_MILLIS) {
            return expectedHour;
        } else if (expectedHour - currentTime < 2 * ONE_HOUR_MILLIS){
            return currentTime + getTimePeriodPercentage((expectedHour - currentTime), 70);
        } else {
            return currentTime + ONE_HOUR_MILLIS;
        }
    }

    private String getLocationAddress(double latitude, double longitude) {
        String address = getString(R.string.current_name_location);

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            if(addresses.get(0).getSubLocality() != null) address = addresses.get(0).getSubLocality();
            else if(addresses.get(0).getLocality() != null) address = addresses.get(0).getLocality();
            else if(addresses.get(0).getCountryName() != null) address = addresses.get(0).getCountryName();
        }

        return address;
    }

    private long getTimePeriodPercentage(long time, int percentage) {
        return time * percentage / 100;
    }

    private int getIconFromWeather(Weather weather) {
        return Constants.FORECAST_ICONS.containsKey(weather.getType())
                ? Constants.FORECAST_ICONS.get(weather.getType())
                : Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);
    }
}
