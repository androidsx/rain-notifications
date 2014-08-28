package com.androidsx.rainnotifications.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.androidsx.rainnotifications.CheckForecast;
import com.androidsx.rainnotifications.CheckForecastException;
import com.androidsx.rainnotifications.CheckForecastResultListener;
import com.androidsx.rainnotifications.UserLocation;
import com.androidsx.rainnotifications.UserLocationException;
import com.androidsx.rainnotifications.UserLocationResultListener;
import com.androidsx.rainnotifications.ForecastMobile;
import com.androidsx.rainnotifications.WearManager;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.NodeApi;

import org.joda.time.DateTimeConstants;
import org.joda.time.Period;

/**
 * This service is responsible to make API calls to forecast.io
 * Once it starts, make an API call to forecast.io with the obtained coordinates.
 *
 * The response is analyzed for determine the next alarm time, and if it's appropriate
 * notify to user the next significant weather change.
 */

public class WeatherService extends Service implements UserLocationResultListener, CheckForecastResultListener {

    private static final String TAG = WeatherService.class.getSimpleName();

    private static final long ONE_HOUR_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    private Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        new UserLocation(this, this);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onLocationSuccess(Location location, String address) {
        CheckForecast.requestForecastForLocation(this, intent, location.getLongitude(), location.getLatitude(), address, this);
    }

    @Override
    public void onForecastSuccess(ForecastTable forecastTable, String address) {
        Weather currentWeather = forecastTable.getBaselineWeather();
        Forecast forecast = null;
        if (!forecastTable.getForecasts().isEmpty()) {
            forecast = forecastTable.getForecasts().get(0);
        }
        final Alert alert = new AlertGenerator().generateAlert(currentWeather, forecast);
        String title = UiUtil.getDebugOnlyPeriodFormatter().print(new Period(forecast.getTimeFromNow()));
        String text = alert.getAlertMessage().toString();
        if(shouldLaunchNotification(CheckForecast.nextWeatherCallAlarmTime(forecast.getTimeFromNow()).toDurationMillis())) {
            launchNotification(
                    title,
                    text,
                    CheckForecast.getIconFromWeather(currentWeather),
                    CheckForecast.getIconFromWeather(forecast.getForecastedWeather())
            );
        }
    }

    @Override
    public void onLocationFailure(UserLocationException exception) {

    }

    @Override
    public void onForecastFailure(CheckForecastException exception) {

    }

    /**
     * Method for determine if a notification is launched,
     * depending on the next alarm time period passed as a param.
     *
     * @param nextAlarmTimePeriod
     */
    private boolean shouldLaunchNotification(long nextAlarmTimePeriod) {
        if (nextAlarmTimePeriod < ONE_HOUR_MILLIS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method for send a wear notification, using a proper message determined by
     * current and forecast weather.
     *
     * @param title
     * @param text
     * @param mascotIcon
     * @param forecastIcon
     */
    private void launchNotification(final String title, final String text, final int mascotIcon, final int forecastIcon) {
        new WearManager(this) {
            @Override
            public void onConnected(Bundle bundle) {
                getConnectedNodes();
            }

            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                if (getConnectedNodesResult.getNodes() != null) {
                    if (getConnectedNodesResult.getNodes().size() > 0) {
                        sendWearNotification(
                                title,
                                text,
                                mascotIcon,
                                forecastIcon);
                    } else {
                        NotificationHelper.sendNotification(
                                WeatherService.this,
                                ForecastMobile.class,
                                title,
                                text,
                                BitmapFactory.decodeResource(getResources(), mascotIcon)
                        );
                    }
                } else {
                    NotificationHelper.sendNotification(
                            WeatherService.this,
                            ForecastMobile.class,
                            title,
                            text,
                            BitmapFactory.decodeResource(getResources(), mascotIcon)
                    );                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }

            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        }.connect();
    }
}
