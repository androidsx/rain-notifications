package com.androidsx.rainnotifications.model;

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
import java.util.List;

/**
 * Tests for the generation of the day summary, {@link com.androidsx.rainnotifications.alert.DayTemplateGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class ForecastTableToDayConverterTest {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
    }

    @Test
    public void testDayMessageForMixDay() {
        DateTime today6am = new DateTime(2014, 9, 28, 6, 0, 0);
        DateTime today8am = new DateTime(2014, 9, 28, 8, 0, 0);
        DateTime today9am = new DateTime(2014, 9, 28, 9, 0, 0);
        DateTime today11am = new DateTime(2014, 9, 28, 11, 0, 0);
        DateTime today14pm = new DateTime(2014, 9, 28, 14, 0, 0);
        DateTime today17pm = new DateTime(2014, 9, 28, 17, 0, 0);
        DateTime today20pm = new DateTime(2014, 9, 28, 20, 0, 0);
        DateTime today22pm = new DateTime(2014, 9, 28, 22, 0, 0);

        ArrayList<Forecast> forecastList = new ArrayList<Forecast>();
        forecastList.add(new Forecast(new Interval(today6am, today8am), new WeatherWrapper(WeatherType.CLOUDY, 0f, WeatherWrapper.TemperatureScale.CELSIUS))); // MORNING
        forecastList.add(new Forecast(new Interval(today8am, today9am), new WeatherWrapper(WeatherType.CLEAR, 0f, WeatherWrapper.TemperatureScale.CELSIUS))); // MORNING
        forecastList.add(new Forecast(new Interval(today9am, today11am), new WeatherWrapper(WeatherType.RAIN, 0f, WeatherWrapper.TemperatureScale.CELSIUS))); // MORNING
        forecastList.add(new Forecast(new Interval(today11am, today14pm), new WeatherWrapper(WeatherType.CLOUDY, 0f, WeatherWrapper.TemperatureScale.CELSIUS))); // MORNING and AFTERNOON
        forecastList.add(new Forecast(new Interval(today14pm, today17pm), new WeatherWrapper(WeatherType.CLOUDY, 0f, WeatherWrapper.TemperatureScale.CELSIUS))); // AFTERNOON
        forecastList.add(new Forecast(new Interval(today17pm, today20pm), new WeatherWrapper(WeatherType.RAIN, 0f, WeatherWrapper.TemperatureScale.CELSIUS))); // AFTERNOON and EVENING
        forecastList.add(new Forecast(new Interval(today20pm, today22pm), new WeatherWrapper(WeatherType.CLEAR, 0f, WeatherWrapper.TemperatureScale.CELSIUS))); // EVENING and night

        Day summary = new Day(ForecastTable.fromForecastList(forecastList));

        Assert.assertEquals(summary.getWeatherType(DayPeriod.MORNING, WeatherPriority.primary), WeatherType.RAIN);
        Assert.assertEquals(summary.getWeatherType(DayPeriod.MORNING, WeatherPriority.secondary), null);

        Assert.assertEquals(summary.getWeatherType(DayPeriod.AFTERNOON, WeatherPriority.primary), WeatherType.CLOUDY);
        Assert.assertEquals(summary.getWeatherType(DayPeriod.AFTERNOON, WeatherPriority.secondary), WeatherType.RAIN);

        Assert.assertEquals(summary.getWeatherType(DayPeriod.EVENING, WeatherPriority.primary), WeatherType.RAIN);
        Assert.assertEquals(summary.getWeatherType(DayPeriod.EVENING, WeatherPriority.secondary), null);
    }

    @Test
    public void testDayMessageForSunnyDayGeneratedAt6am() {
        DateTime today6am = new DateTime(2014, 9, 28, 6, 0, 0);
        WeatherType currentWeatherType = WeatherType.CLEAR;

        ArrayList<Forecast> forecastList = new ArrayList<Forecast>();
        forecastList.add(new Forecast(new Interval(today6am, today6am.plus(Period.hours(24))), new WeatherWrapper(currentWeatherType, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));

        ForecastTable table = ForecastTable.fromForecastList(forecastList);
        Day summary = new Day(table);
        for (DayPeriod dayPeriod : DayPeriod.values()) {
            Assert.assertEquals("Wrong primary for " + dayPeriod, WeatherType.CLEAR, summary.getWeatherType(dayPeriod, WeatherPriority.primary));
            Assert.assertTrue("Wrong secondary for " + dayPeriod, summary.getWeatherType(dayPeriod, WeatherPriority.secondary) == null);
        }
    }

    @Test
    public void testDayMessageForSunnyDayGeneratedAt10am() {
        DateTime today10am = new DateTime(2014, 9, 28, 10, 0, 0);
        WeatherType currentWeatherType = WeatherType.CLEAR;

        ArrayList<Forecast> forecastList = new ArrayList<Forecast>();
        forecastList.add(new Forecast(new Interval(today10am, today10am.plus(Period.hours(24))), new WeatherWrapper(currentWeatherType, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));

        Day summary = new Day(ForecastTable.fromForecastList(forecastList));
        for (DayPeriod dayPeriod : DayPeriod.values()) {
            Assert.assertEquals("Wrong primary for " + dayPeriod, WeatherType.CLEAR, summary.getWeatherType(dayPeriod, WeatherPriority.primary));
            Assert.assertTrue("Wrong secondary for " + dayPeriod, summary.getWeatherType(dayPeriod, WeatherPriority.secondary) == null);
        }
    }

    @Test
    public void testEasyCases() {
        final DateTime today9am = new DateTime(2014, 9, 17, 9, 0);
        final List<Forecast> forecasts = new ArrayList<Forecast>();

        // The day starts off sunny, but just for 30 minutes
        final WeatherType currentWeather = WeatherType.CLEAR;
        final DateTime sunnyEnd = today9am.plus(Period.minutes(30));
        forecasts.add(new Forecast(new Interval(today9am, sunnyEnd), new WeatherWrapper(currentWeather, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));

        // It gets cloudy for most of the MORNING
        final DateTime cloudyStart = sunnyEnd;
        final DateTime cloudyEnd = cloudyStart.plus(Period.hours(2));
        forecasts.add(new Forecast(new Interval(cloudyStart, cloudyEnd), new WeatherWrapper(WeatherType.CLOUDY, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));

        // And then rains for hours
        final DateTime rainStart = today9am.plus(Period.minutes(30)).plus(Period.hours(2));
        final DateTime rainEnd = rainStart.plus(Period.hours(10));
        forecasts.add(new Forecast(new Interval(rainStart, rainEnd), new WeatherWrapper(WeatherType.RAIN, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));

        // Compute the day summary
        final ForecastTable forecastTable = ForecastTable.fromForecastList(forecasts);
        Day day = new Day(forecastTable);

        // Check the results
        Assert.assertEquals(day.getWeatherType(DayPeriod.MORNING, WeatherPriority.primary), WeatherType.CLOUDY);
        Assert.assertEquals(day.getWeatherType(DayPeriod.MORNING, WeatherPriority.secondary), WeatherType.RAIN);
        Assert.assertEquals(day.getWeatherType(DayPeriod.AFTERNOON, WeatherPriority.primary), WeatherType.RAIN);
        Assert.assertTrue(day.getWeatherType(DayPeriod.AFTERNOON, WeatherPriority.secondary) == null);
    }
}
