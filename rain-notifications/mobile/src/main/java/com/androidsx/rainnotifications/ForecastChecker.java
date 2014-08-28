package com.androidsx.rainnotifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.util.AlarmHelper;
import com.androidsx.rainnotifications.util.LocationHelper;

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
     * @param checkForecastResultListener
     */
    public static void requestForecastForLocation(final Context context, final Intent mIntent,
                                                  final double latitude, final double longitude,
                                                  final String address, final CheckForecastResultListener checkForecastResultListener) {
        if (LocationHelper.rightCoordinates(latitude, longitude)) {
            final PendingIntent weatherAlarmIntent;
            if(mIntent != null) {
                weatherAlarmIntent = PendingIntent.getService(context, Constants.AlarmId.WEATHER_ID, mIntent, 0);
            } else {
                weatherAlarmIntent = PendingIntent.getService(context, Constants.AlarmId.WEATHER_ID, new Intent(context, WeatherService.class), 0);
            }
            new ForecastIoNetworkServiceTask() {
                @Override
                protected void onSuccess(ForecastTable forecastTable) {
                    // TODO: Here is where we should apply our logic
                    final Weather currentWeather = forecastTable.getBaselineWeather();
                    AlarmHelper.setAlarm(
                            context,
                            weatherAlarmIntent,
                            currentWeather,
                            forecastTable.getForecasts()
                    );
                    checkForecastResultListener.onForecastSuccess(forecastTable, address);
                }
                @Override
                protected void onFailure() {
                    checkForecastResultListener.onForecastFailure(new CheckForecastException());
                    // TODO: And here is where we do something smart about failures
                }
            }.execute(new ForecastIoRequest(latitude, longitude).getRequest());
        }
    }
}
