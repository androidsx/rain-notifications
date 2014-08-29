package com.androidsx.rainnotifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.util.AlarmHelper;
import com.androidsx.rainnotifications.util.LocationHelper;

/**
 * Class that calls for obtain the weather forecast.
 */
public class ForecastChecker {

    private static final String TAG = ForecastChecker.class.getSimpleName();

    /**
     * Asynchronous method that call for obtain the weather forecast into a determined location.
     *
     * @param context
     * @param mIntent
     * @param latitude
     * @param longitude
     * @param address
     * @param forecastCheckerResultListener
     */
    public static void requestForecastForLocation(final Context context, final Intent mIntent,
                                                  final double latitude, final double longitude,
                                                  final String address, final ForecastCheckerResultListener forecastCheckerResultListener) {
        if (LocationHelper.rightCoordinates(latitude, longitude)) {
            final PendingIntent weatherAlarmIntent = PendingIntent.getService(context, Constants.AlarmId.WEATHER_ID, mIntent, 0);
            new ForecastIoNetworkServiceTask() {
                @Override
                protected void onSuccess(ForecastTable forecastTable) {
                    final Weather currentWeather = forecastTable.getBaselineWeather();
                    AlarmHelper.setAlarm(
                            context,
                            weatherAlarmIntent,
                            currentWeather,
                            forecastTable.getForecasts()
                    );
                    forecastCheckerResultListener.onForecastSuccess(forecastTable, address);
                }
                @Override
                protected void onFailure() {
                    forecastCheckerResultListener.onForecastFailure(new ForecastCheckerException());
                }
            }.execute(new ForecastIoRequest(latitude, longitude).getRequest());
        }
    }
}
