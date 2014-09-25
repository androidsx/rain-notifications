package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.DaySummary;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherPriority;
import com.androidsx.rainnotifications.model.WeatherType;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Tests for the generation of the day summary, {@link com.androidsx.rainnotifications.alert.DaySummaryGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class ForecastTableToDaySummaryConverterTest {

    private static final int MORNING_START_HOUR = 7;
    private static final int MORNING_END_HOUR = 12;
    private static final int AFTERNOON_START_HOUR = 12;
    private static final int AFTERNOON_END_HOUR = 18;
    private static final int EVENING_START_HOUR = 18;
    private static final int EVENING_END_HOUR = 21;
    private static final int NIGHT_START_HOUR = 21;
    private static final int NIGHT_END_HOUR = 7;

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
    }

    // TODO: Reimplement as soon as we create the real DayMessageGenerator
    @Test
    public void testEasyCases() {
        final DateTime today9am = new DateTime(2014, 9, 17, 9, 0);
        final Weather currentWeather = new Weather(WeatherType.CLEAR);
        final List<Forecast> forecasts = new ArrayList<Forecast>();
        forecasts.add(new Forecast(new Weather(WeatherType.CLOUDY), new Interval(today9am.plus(Period.minutes(30)), today9am.plus(Period.hours(2))), Forecast.Granularity.HOUR));
        forecasts.add(new Forecast(new Weather(WeatherType.RAIN), new Interval(today9am.plus(Period.hours(2)), today9am.plus(Period.hours(10))), Forecast.Granularity.HOUR));
        final ForecastTable forecastTable = ForecastTable.create(currentWeather, today9am, null, null, forecasts);

        DaySummary daySummary = getDaySummary(forecastTable);

        Assert.assertEquals(daySummary.getWeather(DayPeriod.morning, WeatherPriority.primary).getType(), WeatherType.RAIN);
        Assert.assertEquals(daySummary.getWeather(DayPeriod.morning, WeatherPriority.secondary).getType(), WeatherType.CLOUDY);

        Assert.assertEquals(daySummary.getWeather(DayPeriod.afternoon, WeatherPriority.primary).getType(), WeatherType.RAIN);
        Assert.assertEquals(daySummary.getWeather(DayPeriod.afternoon, WeatherPriority.secondary).getType(), WeatherType.UNDEFINED);
    }

    public DaySummary getDaySummary(ForecastTable forecastTable) {
        Forecast baselineForecast = createForecastFromBaseline(forecastTable);
        forecastTable.getForecasts().add(baselineForecast);

        System.out.println("Forecasts: " + forecastTable.getForecasts());

        List<Forecast> morningForecasts = filterForecasts(forecastTable.getForecasts(), MORNING_START_HOUR, MORNING_END_HOUR);
        HashMap<WeatherPriority, DaySummary.WeatherWrapper> morningSummary = summarizeForecasts(morningForecasts);

        List<Forecast> afternoonForecasts = filterForecasts(forecastTable.getForecasts(), AFTERNOON_START_HOUR, AFTERNOON_END_HOUR);
        HashMap<WeatherPriority, DaySummary.WeatherWrapper> afternoonSummary = summarizeForecasts(afternoonForecasts);

        DaySummary.DaySummaryBuilder builder = new DaySummary.DaySummaryBuilder();
        builder.setWeatherWrapper(DayPeriod.morning, WeatherPriority.primary, morningSummary.get(WeatherPriority.primary));
        builder.setWeatherWrapper(DayPeriod.morning, WeatherPriority.secondary, morningSummary.get(WeatherPriority.secondary));
        builder.setWeatherWrapper(DayPeriod.afternoon, WeatherPriority.primary, afternoonSummary.get(WeatherPriority.primary));
        builder.setWeatherWrapper(DayPeriod.afternoon, WeatherPriority.secondary, afternoonSummary.get(WeatherPriority.secondary));

        return builder.build();
    }
    

    private List<Forecast> filterForecasts(List<Forecast> forecasts, int startHourOfDayLimit, int endHourOfDayLimit) {
        ArrayList<Forecast> filteredForecasts = new ArrayList<Forecast>();
        for (Forecast forecast : forecasts) {
            int startHourOfDayForecast = new DateTime(forecast.getTimeFromNow().getStartMillis()).getHourOfDay();
            int endHourOfDayForecast = new DateTime(forecast.getTimeFromNow().getEndMillis()).getHourOfDay();
            System.out.println("Forecast hours: [ " + startHourOfDayForecast + " , " + endHourOfDayForecast + " ]");
            // The start or end hour within the limits, or overlapping across outside the limits
            if ((startHourOfDayForecast >= startHourOfDayLimit && startHourOfDayForecast <= endHourOfDayLimit) ||
                    (endHourOfDayForecast >= startHourOfDayLimit && endHourOfDayForecast <= endHourOfDayLimit) ||
                    (endHourOfDayForecast >= endHourOfDayLimit && startHourOfDayForecast <= endHourOfDayLimit)) {
                System.out.println("Within the limit: [ " + startHourOfDayLimit + " - " + endHourOfDayLimit + " ]");
                filteredForecasts.add(forecast);
            }
        }
        return filteredForecasts;
    }

    private HashMap<WeatherPriority, DaySummary.WeatherWrapper> summarizeForecasts(List<Forecast> forecasts) {
        HashMap<WeatherPriority, DaySummary.WeatherWrapper> priorityWeathers = new HashMap<WeatherPriority, DaySummary.WeatherWrapper>();
        priorityWeathers.put(WeatherPriority.primary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
        priorityWeathers.put(WeatherPriority.secondary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));

        if (forecasts.size() == 0) {
            return priorityWeathers;
        } else if (forecasts.size() == 1) {
            priorityWeathers.put(WeatherPriority.primary, new DaySummary.WeatherWrapper(forecasts.get(0).getForecastedWeather().getType()));
            return priorityWeathers;
        } else {
            // Sort by the bigger interval of time
            Collections.sort(forecasts, new Comparator<Forecast>() {
                @Override
                public int compare(Forecast o1, Forecast o2) {
                    if (o1.getTimeFromNow().isBefore(o2.getTimeFromNow())) {
                        return 1;
                    } else if (o1.getTimeFromNow().isEqual(o2.getTimeFromNow())) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            // TODO: if there are more than 2, check if the last forecast has more importance (such as rain)
            priorityWeathers.put(WeatherPriority.primary, new DaySummary.WeatherWrapper(forecasts.get(0).getForecastedWeather().getType()));
            priorityWeathers.put(WeatherPriority.secondary, new DaySummary.WeatherWrapper(forecasts.get(1).getForecastedWeather().getType()));
        }

        return priorityWeathers;
    }

    private Forecast createForecastFromBaseline(ForecastTable forecastTable) {
        final Interval baselineInterval;
        if (forecastTable.getForecasts().size() > 0) {
            Interval firstForecastInterval = forecastTable.getForecasts().get(0).getTimeFromNow();
            baselineInterval = new Interval(forecastTable.getBaselineTime(), new DateTime(firstForecastInterval.getStartMillis()));
        } else {
            baselineInterval = new Interval(forecastTable.getBaselineTime(), forecastTable.getBaselineTime().plus(Period.days(1)));
        }
        return new Forecast(forecastTable.getBaselineWeather(), baselineInterval, Forecast.Granularity.HOUR);
    }
}
