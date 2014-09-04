package com.androidsx.rainnotifications.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.service.WeatherService;

import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.List;

import timber.log.Timber;

public class AlarmHelper {
    private static final String TAG = AlarmHelper.class.getSimpleName();

    private static final long WEATHER_REPEATING_TIME_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    public static final long TEN_MINUTES_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long ONE_HOUR_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long DEFAULT_EXTRA_TIME_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    private AlarmHelper() {
        //No-instantiate
    }

    /**
     * Method that set alarm next, depending on weather forecasts list.
     *
     * @param context
     * @param weatherAlarmIntent
     * @param currentWeather
     * @param forecastList
     */
    public static void setAlarm(Context context, PendingIntent weatherAlarmIntent, Weather currentWeather, List<Forecast> forecastList) {
        Interval nextIntervalAlarmTime;
        if (forecastList.isEmpty()) {
            nextIntervalAlarmTime = new Interval(System.currentTimeMillis(), System.currentTimeMillis() + DEFAULT_EXTRA_TIME_MILLIS);
        } else {
            nextIntervalAlarmTime = forecastList.get(0).getTimeFromNow();
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
            if (!forecastList.isEmpty()) {
                Timber.tag(TAG).i("Next transition is %s -> %s in %s.",
                        currentWeather.getType(),
                        forecastList.get(0).getForecastedWeather().getType(),
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(forecastList.get(0).getTimeFromNow()))
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
        if (interval.toDurationMillis() < TEN_MINUTES_MILLIS) {
            return interval;
        } else if (interval.toDurationMillis() < 2 * ONE_HOUR_MILLIS){
            return new Interval(interval.getStartMillis(),
                    interval.getStartMillis() + getTimePeriodPercentage(interval.toDurationMillis(), 70));
        } else {
            return new Interval(interval.getStartMillis(), interval.getStartMillis() + ONE_HOUR_MILLIS);
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
}
