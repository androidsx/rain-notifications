package com.androidsx.rainnotifications.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;

import com.androidsx.rainnotifications.ForecastChecker;
import com.androidsx.rainnotifications.ForecastCheckerException;
import com.androidsx.rainnotifications.ForecastCheckerResultListener;
import com.androidsx.rainnotifications.UserLocation;
import com.androidsx.rainnotifications.UserLocationException;
import com.androidsx.rainnotifications.ForecastMobile;
import com.androidsx.rainnotifications.WearNotificationManager;
import com.androidsx.rainnotifications.WearNotificationManagerException;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.util.AlarmHelper;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.androidsx.rainnotifications.util.WeatherHelper;
import com.google.android.gms.wearable.NodeApi;

import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;

/**
 * This service is responsible to make API calls to forecast.io
 * Once it starts, make an API call to forecast.io with the obtained coordinates.
 *
 * The response is analyzed for determine the next alarm time, and if it's appropriate
 * notify to user the next significant weather change.
 */

public class WeatherService extends Service {

    private static final String TAG = WeatherService.class.getSimpleName();

    private static final long ONE_HOUR_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new UserLocation(this) {
            @Override
            public void onLocationSuccess(Location location) {
                Intent mIntent = intent;
                if(mIntent == null) {
                    mIntent = new Intent(WeatherService.this, WeatherService.class);
                }
                ForecastChecker.requestForecastForLocation(WeatherService.this, mIntent, location.getLatitude(), location.getLongitude(),
                        new ForecastCheckerResultListener() {
                    @Override
                    public void onForecastSuccess(ForecastTable forecastTable) {
                        if (!forecastTable.getForecasts().isEmpty()) {
                            Weather currentWeather = forecastTable.getBaselineWeather();
                            Forecast forecast = forecastTable.getForecasts().get(0);
                            final Alert alert = new AlertGenerator().generateAlert(currentWeather, forecast);
                            String text = alert.getAlertMessage().toString();
                            if(shouldLaunchNotification(AlarmHelper.nextWeatherCallAlarmTime(forecast.getTimeFromNow()))) {
                                launchNotification(
                                        text,
                                        WeatherHelper.getIconFromWeather(forecast.getForecastedWeather())
                                );
                            }
                        }
                    }

                    @Override
                    public void onForecastFailure(ForecastCheckerException exception) {

                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationException exception) {

            }
        }.connect();

        return super.onStartCommand(intent, flags, startId);
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

    /**
     * Method for send a wear notification, using a proper message determined by
     * current and forecast weather.
     *
     * @param text
     * @param mascotIcon
     */
    private void launchNotification(final String text, final int mascotIcon) {
        new WearNotificationManager(this) {
            @Override
            public void onWearManagerSuccess(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                if (getConnectedNodesResult.getNodes() != null) {
                    if (getConnectedNodesResult.getNodes().size() > 0) {
                        sendWearNotification(
                                WeatherService.this,
                                text,
                                mascotIcon
                        );
                    } else {
                        NotificationHelper.sendNotification(
                                WeatherService.this,
                                ForecastMobile.class,
                                text,
                                BitmapFactory.decodeResource(getResources(), mascotIcon)
                        );
                    }
                } else {
                    NotificationHelper.sendNotification(
                            WeatherService.this,
                            ForecastMobile.class,
                            text,
                            BitmapFactory.decodeResource(getResources(), mascotIcon)
                    );
                }
            }

            @Override
            public void onWearManagerFailure(WearNotificationManagerException exception) {

            }
        }.connect();
    }
}
