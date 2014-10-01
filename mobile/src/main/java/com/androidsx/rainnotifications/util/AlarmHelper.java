package com.androidsx.rainnotifications.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.service.WeatherService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.TimeZone;

import timber.log.Timber;

public class AlarmHelper {
    private static final String TAG = AlarmHelper.class.getSimpleName();
    public static final String NEXT_ALARM_TIME = "next_alarm_time";
    public static final String DAY_ALARM_TIME = "day_alarm_time";

    private AlarmHelper() {
        //No-instantiate
    }

    /**
     * Sets the following alarm for the weather service, that depends on the time to the first
     * expected weather transition. If there are no weather transitions, it's set an hour from now.
     *
     * @see #computeNextAlarmTime(com.androidsx.rainnotifications.model.ForecastTable)
     */
    public static void setNextAlarm(Context context, PendingIntent weatherAlarmIntent, DateTime nextAlarmTime, ForecastTable forecastTable) {
        weatherAlarmIntent.cancel();
        weatherAlarmIntent = PendingIntent.getService(
                context,
                Constants.Alarms.WEATHER_ID,
                new Intent(context, WeatherService.class),
                0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(weatherAlarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTime.getMillis(),
                    10 * DateTimeConstants.MILLIS_PER_MINUTE,
                    weatherAlarmIntent);
            SharedPrefsHelper.saveLongValue(context, NEXT_ALARM_TIME, nextAlarmTime.getMillis());
            if (forecastTable.hasTransitions()) {
                Timber.tag(TAG).i("Next transition is %s -> %s in %s.",
                        forecastTable.getForecastList().get(0).getWeatherWrapper().getType(),
                        forecastTable.getForecastList().get(1).getWeatherWrapper().getType(),
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(new Interval(System.currentTimeMillis(), nextAlarmTime.getMillis())))
                );
                Timber.tag(TAG).i("Schedule an alarm for %s from now. Bye!",
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(new Interval(System.currentTimeMillis(), nextAlarmTime.getMillis())))
                );
            } else {
                Timber.tag(TAG).i("Schedule an alarm for %s from now, we don't expect changes. Bye!",
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(new Interval(System.currentTimeMillis(), nextAlarmTime.getMillis())))
                );
            }
        }
        Timber.tag(TAG).i("***********************");
    }

    public static void setDayAlarm(Context context, int hour, PendingIntent weatherAlarmIntent) {
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
        DateTime alarmTime = new DateTime();
        alarmTime = alarmTime.hourOfDay().setCopy(hour).minuteOfHour().setCopy(0).secondOfMinute().setCopy(0).millisOfSecond().setCopy(0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(weatherAlarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime.getMillis(),
                    DateTimeConstants.MILLIS_PER_DAY,
                    weatherAlarmIntent);
        }
        SharedPrefsHelper.saveLongValue(context, DAY_ALARM_TIME, alarmTime.getMillis());
    }

    public static void setFirstNextAlarm(Context context, PendingIntent alarmIntent) {
        DateTime firstAlarm = new DateTime(DateTime.now());
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(alarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    firstAlarm.getMillis(),
                    10 * DateTimeConstants.MILLIS_PER_MINUTE,
                    alarmIntent);
        }
        SharedPrefsHelper.saveLongValue(context, NEXT_ALARM_TIME, firstAlarm.getMillis());
    }

    /**
     * Returns an appropriate time for the next alarm, given the interval to the next relevant
     * event (usually a weather transition we care about). The logic is:
     *
     * <ol>
     * <li>Less than 90 minutes away: set it an hour from now</li>
     * <li>Other cases: set it at 70% of the time between now and the event</li>
     * </ol>
     *
     * @param forecastTable Forecast table that contains all the weather transitions
     * @return DateTime next alarm time
     */
    public static DateTime computeNextAlarmTime(ForecastTable forecastTable) {
        Interval nextIntervalAlarmTime;
        if (forecastTable.hasTransitions()) {
            nextIntervalAlarmTime = new Interval(forecastTable.getStart().getMillis(), forecastTable.getForecastList().get(1).getInterval().getStartMillis() + DateTimeConstants.MILLIS_PER_HOUR);
        } else {
            nextIntervalAlarmTime = new Interval(forecastTable.getStart().getMillis(), forecastTable.getStart().getMillis() + DateTimeConstants.MILLIS_PER_HOUR);
        }
        if (nextIntervalAlarmTime.toDurationMillis() < 90 * DateTimeConstants.MILLIS_PER_MINUTE) {
            return new DateTime(nextIntervalAlarmTime.getStartMillis() + DateTimeConstants.MILLIS_PER_HOUR);
        } else {
            return new DateTime(nextIntervalAlarmTime.getStartMillis() + getTimePeriodPercentage(nextIntervalAlarmTime.toDurationMillis(), 70));
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
