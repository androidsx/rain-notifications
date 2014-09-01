package com.androidsx.rainnotifications.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.util.UserLocationFetcher;
import com.androidsx.rainnotifications.util.ForecastChecker;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.util.AlarmHelper;
import com.androidsx.rainnotifications.util.NotificationHelper;

import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;

import timber.log.Timber;

/**
 * This service is responsible to make API calls to forecast.io
 * Once it starts, make an API call to forecast.io with the obtained coordinates.
 *
 * The response is analyzed for determine the next alarm time, and if it's appropriate
 * notify to user the next significant weather change.
 */

public class WeatherService extends Service {
    private static final long ONE_HOUR_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // FIXME: we do exactly the same in the mobile activity!
        new UserLocationFetcher(this, new UserLocationFetcher.UserLocationResultListener() {
            @Override
            public void onLocationSuccess(Location location) {
                ForecastChecker.requestForecastForLocation(location.getLatitude(), location.getLongitude(),
                        new ForecastChecker.ForecastCheckerResultListener() {
                    @Override
                    public void onForecastSuccess(ForecastTable forecastTable) {
                        if (forecastTable.getForecasts().isEmpty()) {
                            Timber.d("No transitions are expected, so there's no notifications to generate");
                        } else {
                            final Forecast forecast = forecastTable.getForecasts().get(0);
                            final Alert alert = new AlertGenerator().generateAlert(forecastTable.getBaselineWeather(), forecast);
                            if (shouldLaunchNotification(AlarmHelper.nextWeatherCallAlarmTime(forecast.getTimeFromNow()))) {
                                Timber.i("Will display notification for " + alert);
                                NotificationHelper.displayCustomNotification(WeatherService.this, alert);
                            } else {
                                Timber.d("No notification for now. The alert was " + alert);
                            }
                        }

                        setNextAlarm(forecastTable);
                    }

                    @Override
                    public void onForecastFailure(ForecastChecker.ForecastCheckerException exception) {
                        throw new IllegalStateException("Failed to get the forecast", exception); // FIXME: set the next alarm a little from now?
                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationFetcher.UserLocationException exception) {
                throw new IllegalStateException("Failed to get the location", exception); // FIXME: set the next alarm a little from now?
            }
        }).connect();

        return super.onStartCommand(intent, flags, startId);
    }

    private void setNextAlarm(ForecastTable forecastTable) {
        final PendingIntent weatherAlarmIntent = PendingIntent.getService(
                WeatherService.this,
                Constants.AlarmId.WEATHER_ID,
                new Intent(WeatherService.this, WeatherService.class),
                0);
        AlarmHelper.setAlarm(
                WeatherService.this,
                weatherAlarmIntent,
                forecastTable.getBaselineWeather(),
                forecastTable.getForecasts()
        );
    }

    /**
     * Method for determine if a notification is launched,
     * depending on the next alarm time period passed as a param.
     *
     * @param nextAlarmTimePeriod
     */
    private boolean shouldLaunchNotification(Interval nextAlarmTimePeriod) {
        if (nextAlarmTimePeriod.toDurationMillis() < ONE_HOUR_MILLIS) {
            return true;
        } else {
            return false;
        }
    }
}
