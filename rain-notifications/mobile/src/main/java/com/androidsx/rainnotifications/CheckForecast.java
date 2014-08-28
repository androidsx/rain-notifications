package com.androidsx.rainnotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.util.LocationHelper;

import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.List;

import timber.log.Timber;

public class CheckForecast {

    private static final String TAG = CheckForecast.class.getSimpleName();

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
                    scheduleAlarm(
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
     * Method that set alarm next, depending on weather forecasts list.
     *
     * @param currentWeather
     * @param forecasts
     */
    private static void scheduleAlarm(Context context, PendingIntent weatherAlarmIntent, Weather currentWeather, List<com.androidsx.rainnotifications.model.Forecast> forecasts) {
        Interval nextIntervalAlarmTime;
        if (forecasts.isEmpty()) {
            nextIntervalAlarmTime = new Interval(System.currentTimeMillis(), System.currentTimeMillis() + DEFAULT_EXTRA_TIME_MILLIS);
        } else {
            nextIntervalAlarmTime = forecasts.get(0).getTimeFromNow();
        }
        Interval nextAlarmTimePeriod = nextWeatherCallAlarmTime(nextIntervalAlarmTime);

        weatherAlarmIntent.cancel();
        weatherAlarmIntent = PendingIntent.getService(
                context,
                Constants.AlarmId.WEATHER_ID,
                new Intent(context, WeatherService.class),
                0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(weatherAlarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTimePeriod.getEndMillis(),
                    WEATHER_REPEATING_TIME_MILLIS,
                    weatherAlarmIntent);
            if (!forecasts.isEmpty()) {
                Timber.tag(TAG).i("Next transition is %s -> %s in %s.",
                        currentWeather.getType(),
                        forecasts.get(0).getForecastedWeather().getType(),
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(forecasts.get(0).getTimeFromNow()))
                );
                Timber.tag(TAG).i("Schedule an alarm for %s from now. Bye!",
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(nextAlarmTimePeriod))
                );
            } else {
                Timber.tag(TAG).i("Schedule an alarm for %s from now, we don't expect changes. Bye!",
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(nextAlarmTimePeriod))
                );
            }
        }
        Timber.tag(TAG).i("***********************");
    }

    /**
     * This method is for determine the next alarm hour,
     * depending on the interval from now to expected hour passed as a param.
     *
     * @param interval
     * @return long - next alarm hour in millis
     */
    public static Interval nextWeatherCallAlarmTime(Interval interval) {
        final long startTime = interval.getStartMillis();
        final long endTime = interval.getEndMillis();
        if ((endTime - startTime) < TEN_MINUTES_MILLIS) {
            return new Interval(startTime, endTime);
        } else if (endTime - startTime < 2 * ONE_HOUR_MILLIS){
            return new Interval(startTime, startTime + getTimePeriodPercentage((endTime - startTime), 70));
        } else {
            return new Interval(startTime, startTime + ONE_HOUR_MILLIS);
        }
    }

    /**
     * Method for obtain a percentage time in milliseconds of an interval.
     *
     * @param time
     * @param percentage
     * @return long - period in milliseconds
     */
    private static long getTimePeriodPercentage(long time, int percentage) {
        return time * percentage / 100;
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
