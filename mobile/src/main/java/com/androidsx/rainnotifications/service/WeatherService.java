package com.androidsx.rainnotifications.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.ui.main.MainMobileActivity;
import com.androidsx.rainnotifications.util.AlarmHelper;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.androidsx.rainnotifications.util.UserLocationFetcher;
import com.androidsx.rainnotifications.weatherclientfactory.WeatherClientFactory;

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
    private AlertGenerator alertGenerator;

    @Override
    public void onCreate() {
        super.onCreate();

        alertGenerator = new AlertGenerator(this);
        alertGenerator.init();
    }

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
                WeatherClientFactory.requestForecastForLocation(WeatherService.this, location.getLatitude(), location.getLongitude(), new WeatherClientResponseListener() {
                    @Override
                    public void onForecastSuccess (ForecastTable forecastTable){
                        if (intent != null && intent.getIntExtra(Constants.Extras.EXTRA_DAY_ALARM, 0) == Constants.Alarms.DAY_ALARM_ID) {
                            //TODO: getDayMessage and send Notification.
                        } else {
                            if (!forecastTable.hasTransitions()) {
                                Timber.d("No transitions are expected, so there's no notifications to generate");
                            } else {
                                final Forecast forecast = forecastTable.getForecastList().get(1);
                                final Alert alert = alertGenerator.generateAlert(forecastTable.getForecastList().get(0).getWeatherWrapper().getType(), forecast.getWeatherWrapper().getType());
                                if (shouldLaunchNotification(forecast.getInterval().getStartMillis() - System.currentTimeMillis())) {
                                    Timber.i("Will display notification for " + alert);
                                    NotificationHelper.displayCustomNotification(WeatherService.this, alert, new Interval(forecastTable.getStart(), forecast.getInterval().getStart()));
                                } else {
                                    Timber.d("No notification for now. The alert was " + alert);
                                }
                            }

                            setNextAlarm(forecastTable);
                        }
                    }

                    @Override
                    public void onForecastFailure (WeatherClientException exception){
                        Timber.e(exception, "Failed to get the forecast");
                        NotificationHelper.displayStandardNotification(WeatherService.this, MainMobileActivity.class, "Failed to get the forecast: " + exception.toString(), BitmapFactory.decodeResource(getResources(), R.drawable.owlie_debug));
                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationFetcher.UserLocationException exception) {
                Timber.e(exception, "Failed to get the location");
                NotificationHelper.displayStandardNotification(WeatherService.this, MainMobileActivity.class, "Failed to get the location" + exception.toString(), BitmapFactory.decodeResource(getResources(), R.drawable.owlie_debug));
            }
        }).connect();

        return START_NOT_STICKY;
    }

    private void setNextAlarm(ForecastTable forecastTable) {
        final PendingIntent weatherAlarmIntent = PendingIntent.getService(
                WeatherService.this,
                Constants.Alarms.WEATHER_ID,
                new Intent(WeatherService.this, WeatherService.class),
                0);
        AlarmHelper.setNextAlarm(
                WeatherService.this,
                weatherAlarmIntent,
                AlarmHelper.computeNextAlarmTime(forecastTable),
                forecastTable
        );
    }

    /**
     * Method for determine if a notification is launched,
     * depending on the next alarm time period passed as a param.
     *
     * @param nextForecastTimePeriod
     */
    private boolean shouldLaunchNotification(long nextForecastTimePeriod) {
        if (nextForecastTimePeriod < ONE_HOUR_MILLIS) {
            return true;
        } else {
            return false;
        }
    }
}
