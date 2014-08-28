package com.androidsx.rainnotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.util.AlarmHelper;
import com.androidsx.rainnotifications.util.LocationHelper;

import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.List;

import timber.log.Timber;

public class ForecastChecker {

    private static final String TAG = ForecastChecker.class.getSimpleName();

    private static final long WEATHER_REPEATING_TIME_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    public static final long TEN_MINUTES_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long ONE_HOUR_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long DEFAULT_EXTRA_TIME_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    /**
     * Asynchronous method that call for obtain the weather forecast into a determined location.
     *
     * @param longitude
     * @param latitude
     */
    public static void requestForecastForLocation(final Context context, final Intent mIntent,
                                                  final double longitude, final double latitude,
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

    /**
     * Method for obtain the resource icon, depending on weather passed as a param, using its type.
     *
     * @param weather
     * @return int - resource id
     */
    public static int getIconFromWeather(Weather weather) {
        if (weather == null) return Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);
        return Constants.FORECAST_ICONS.containsKey(weather.getType())
                ? Constants.FORECAST_ICONS.get(weather.getType())
                : Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);
    }
}
