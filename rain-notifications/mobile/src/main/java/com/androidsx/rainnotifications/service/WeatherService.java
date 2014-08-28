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
import com.androidsx.rainnotifications.UserLocationResultListener;
import com.androidsx.rainnotifications.ForecastMobile;
import com.androidsx.rainnotifications.WearManager;
import com.androidsx.rainnotifications.WearManagerException;
import com.androidsx.rainnotifications.WearManagerResultListener;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.util.AlarmHelper;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.androidsx.rainnotifications.util.WeatherHelper;
import com.google.android.gms.wearable.NodeApi;

import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.Period;

/**
 * This service is responsible to make API calls to forecast.io
 * Once it starts, make an API call to forecast.io with the obtained coordinates.
 *
 * The response is analyzed for determine the next alarm time, and if it's appropriate
 * notify to user the next significant weather change.
 */

public class WeatherService extends Service implements UserLocationResultListener, ForecastCheckerResultListener, WearManagerResultListener {

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
        ForecastChecker.requestForecastForLocation(this, intent, location.getLatitude(), location.getLongitude(), address, this);
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
        if(shouldLaunchNotification(AlarmHelper.nextWeatherCallAlarmTime(forecast.getTimeFromNow()))) {
            launchNotification(
                    title,
                    text,
                    WeatherHelper.getIconFromWeather(currentWeather),
                    WeatherHelper.getIconFromWeather(forecast.getForecastedWeather())
            );
        }
    }

    @Override
    public void onLocationFailure(UserLocationException exception) {

    }

    @Override
    public void onForecastFailure(ForecastCheckerException exception) {

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
     * @param title
     * @param text
     * @param mascotIcon
     * @param forecastIcon
     */
    private void launchNotification(final String title, final String text, final int mascotIcon, final int forecastIcon) {
        new WearManager(this, this, title, text, mascotIcon, forecastIcon).connect();
    }

    @Override
    public void onWearManagerSuccess(NodeApi.GetConnectedNodesResult getConnectedNodesResult, WearManager mWearManager) {
        if (getConnectedNodesResult.getNodes() != null) {
            if (getConnectedNodesResult.getNodes().size() > 0) {
                mWearManager.sendWearNotification();
            } else {
                NotificationHelper.sendNotification(
                        this,
                        ForecastMobile.class,
                        mWearManager.getTitle(),
                        mWearManager.getText(),
                        BitmapFactory.decodeResource(getResources(), mWearManager.getForecastIcon())
                );
            }
        } else {
            NotificationHelper.sendNotification(
                    this,
                    ForecastMobile.class,
                    mWearManager.getTitle(),
                    mWearManager.getText(),
                    BitmapFactory.decodeResource(getResources(), mWearManager.getForecastIcon())
            );
        }
    }

    @Override
    public void onWearManagerFailure(WearManagerException exception) {

    }
}
