package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.DaySummary;
import com.androidsx.rainnotifications.model.DaySummaryV2;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.ForecastTableV2;
import com.androidsx.rainnotifications.model.ForecastV2;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherPriority;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.WeatherWrapperV2;

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

    @Test
    public void testVersion2() {
        DateTime today6am = new DateTime(2014, 9, 28, 6, 0, 0);
        DateTime today8am = new DateTime(2014, 9, 28, 8, 0, 0);
        DateTime today9am = new DateTime(2014, 9, 28, 9, 0, 0);
        DateTime today11am = new DateTime(2014, 9, 28, 11, 0, 0);
        DateTime today14pm = new DateTime(2014, 9, 28, 14, 0, 0);
        DateTime today17pm = new DateTime(2014, 9, 28, 17, 0, 0);
        DateTime today20pm = new DateTime(2014, 9, 28, 20, 0, 0);
        DateTime today22pm = new DateTime(2014, 9, 28, 22, 0, 0);

        ArrayList<ForecastV2> forecastList = new ArrayList<ForecastV2>();
        forecastList.add(new ForecastV2(new Interval(today6am, today8am), new WeatherWrapperV2(WeatherType.CLOUDY))); // morning
        forecastList.add(new ForecastV2(new Interval(today8am, today9am), new WeatherWrapperV2(WeatherType.CLEAR))); // morning
        forecastList.add(new ForecastV2(new Interval(today9am, today11am), new WeatherWrapperV2(WeatherType.RAIN))); // morning
        forecastList.add(new ForecastV2(new Interval(today11am, today14pm), new WeatherWrapperV2(WeatherType.CLOUDY))); // morning and afternoon
        forecastList.add(new ForecastV2(new Interval(today14pm, today17pm), new WeatherWrapperV2(WeatherType.PARTLY_CLOUDY))); // afternoon
        forecastList.add(new ForecastV2(new Interval(today17pm, today20pm), new WeatherWrapperV2(WeatherType.RAIN))); // afternoon and evening
        forecastList.add(new ForecastV2(new Interval(today20pm, today22pm), new WeatherWrapperV2(WeatherType.CLEAR))); // evening and night

        DaySummaryV2 summary = DaySummaryV2.fromForecastTable(new ForecastTableV2(forecastList));

        Assert.assertEquals(summary.getWeatherType(DayPeriod.morning, WeatherPriority.primary), WeatherType.RAIN);
        Assert.assertEquals(summary.getWeatherType(DayPeriod.morning, WeatherPriority.secondary), WeatherType.CLOUDY);

        Assert.assertEquals(summary.getWeatherType(DayPeriod.afternoon, WeatherPriority.primary), WeatherType.PARTLY_CLOUDY);
        Assert.assertEquals(summary.getWeatherType(DayPeriod.afternoon, WeatherPriority.secondary), WeatherType.RAIN);

        Assert.assertEquals(summary.getWeatherType(DayPeriod.evening, WeatherPriority.primary), WeatherType.RAIN);
        Assert.assertEquals(summary.getWeatherType(DayPeriod.evening, WeatherPriority.secondary), WeatherType.CLEAR);

        Assert.assertEquals(summary.getWeatherType(DayPeriod.night, WeatherPriority.primary), WeatherType.CLEAR);
        Assert.assertEquals(summary.getWeatherType(DayPeriod.night, WeatherPriority.secondary), WeatherType.UNDEFINED);
    }

    // Broken. See comment at https://github.com/androidsx/rain-notifications/commit/e3594f173f5316827c7885837d2c2ea14cec1da4#commitcomment-7952897
    @Test
    public void testDayMessageForSunnyDayGeneratedAt6am() {
        // TODO: these two variables are not used
        DateTime today6am = new DateTime(2014, 9, 28, 6, 0, 0);
        WeatherType currentWeatherType = WeatherType.CLEAR;

        ArrayList<ForecastV2> emptyForecastList = new ArrayList<ForecastV2>();
        DaySummaryV2 summary = DaySummaryV2.fromForecastTable(new ForecastTableV2(emptyForecastList));
        Assert.assertEquals(summary.getWeatherType(DayPeriod.morning, WeatherPriority.primary), WeatherType.CLEAR);
        Assert.assertEquals(summary.getWeatherType(DayPeriod.morning, WeatherPriority.secondary), WeatherType.CLEAR);
    }

    // Broken. See previous test
    @Test
    public void testDayMessageForSunnyDayGeneratedAt10am() {
        // TODO: these two variables are not used
        DateTime today10am = new DateTime(2014, 9, 28, 10, 0, 0);
        WeatherType currentWeatherType = WeatherType.CLEAR;

        ArrayList<ForecastV2> emptyForecastList = new ArrayList<ForecastV2>();
        DaySummaryV2 summary = DaySummaryV2.fromForecastTable(new ForecastTableV2(emptyForecastList));
        Assert.assertEquals(summary.getWeatherType(DayPeriod.morning, WeatherPriority.primary), WeatherType.CLEAR);
        Assert.assertEquals(summary.getWeatherType(DayPeriod.morning, WeatherPriority.secondary), WeatherType.CLEAR);
    }

    // TODO: Reimplement as soon as we create the real DayMessageGenerator
    @Test
    public void testEasyCases() {
        final DateTime today9am = new DateTime(2014, 9, 17, 9, 0);
        final List<ForecastV2> forecasts = new ArrayList<ForecastV2>();
        final DateTime cloudyStart = today9am.plus(Period.minutes(30));
        final DateTime cloudyEnd = cloudyStart.plus(Period.hours(2));
        forecasts.add(new ForecastV2(new Interval(cloudyStart, cloudyEnd), new WeatherWrapperV2(WeatherType.CLOUDY)));
        final DateTime rainStart = today9am.plus(Period.minutes(30)).plus(Period.hours(2));
        final DateTime rainEnd = rainStart.plus(Period.hours(10));
        forecasts.add(new ForecastV2(new Interval(rainStart, rainEnd), new WeatherWrapperV2(WeatherType.RAIN)));
        final ForecastTableV2 forecastTable = new ForecastTableV2(forecasts);

        DaySummaryV2 daySummary = DaySummaryV2.fromForecastTable(forecastTable);

        Assert.assertEquals(daySummary.getWeatherType(DayPeriod.morning, WeatherPriority.primary), WeatherType.CLOUDY);
        Assert.assertEquals(daySummary.getWeatherType(DayPeriod.morning, WeatherPriority.secondary), WeatherType.RAIN);

        Assert.assertEquals(daySummary.getWeatherType(DayPeriod.afternoon, WeatherPriority.primary), WeatherType.RAIN);
        Assert.assertEquals(daySummary.getWeatherType(DayPeriod.afternoon, WeatherPriority.secondary), WeatherType.UNDEFINED);
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
