package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.DaySummaryV2;
import com.androidsx.rainnotifications.model.ForecastTableV2;
import com.androidsx.rainnotifications.model.ForecastV2;
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
import java.util.List;

/**
 * Tests for the generation of the day summary, {@link com.androidsx.rainnotifications.alert.DaySummaryGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class ForecastTableToDaySummaryConverterTest {
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

    @Test
    public void testDayMessageForSunnyDayGeneratedAt6am() {
        DateTime today6am = new DateTime(2014, 9, 28, 6, 0, 0);
        WeatherType currentWeatherType = WeatherType.CLEAR;

        ArrayList<ForecastV2> forecastList = new ArrayList<ForecastV2>();
        forecastList.add(new ForecastV2(new Interval(today6am, today6am.plus(Period.hours(24))), new WeatherWrapperV2(currentWeatherType)));

        ForecastTableV2 table = new ForecastTableV2(forecastList);
        DaySummaryV2 summary = DaySummaryV2.fromForecastTable(table);
        for (DayPeriod dayPeriod : DayPeriod.values()) {
            Assert.assertEquals("Wrong primary for " + dayPeriod, WeatherType.CLEAR, summary.getWeatherType(dayPeriod, WeatherPriority.primary));
            Assert.assertEquals("Wrong secondary for " + dayPeriod, WeatherType.UNDEFINED, summary.getWeatherType(dayPeriod, WeatherPriority.secondary));
        }
    }

    @Test
    public void testDayMessageForSunnyDayGeneratedAt10am() {
        DateTime today10am = new DateTime(2014, 9, 28, 10, 0, 0);
        WeatherType currentWeatherType = WeatherType.CLEAR;

        ArrayList<ForecastV2> forecastList = new ArrayList<ForecastV2>();
        forecastList.add(new ForecastV2(new Interval(today10am, today10am.plus(Period.hours(24))), new WeatherWrapperV2(currentWeatherType)));

        DaySummaryV2 summary = DaySummaryV2.fromForecastTable(new ForecastTableV2(forecastList));
        for (DayPeriod dayPeriod : DayPeriod.values()) {
            Assert.assertEquals("Wrong primary for " + dayPeriod, WeatherType.CLEAR, summary.getWeatherType(dayPeriod, WeatherPriority.primary));
            Assert.assertEquals("Wrong secondary for " + dayPeriod, WeatherType.UNDEFINED, summary.getWeatherType(dayPeriod, WeatherPriority.secondary));
        }
    }

    // TODO: Reimplement as soon as we create the real DayMessageGenerator
    @Test
    public void testEasyCases() {
        final DateTime today9am = new DateTime(2014, 9, 17, 9, 0);
        final List<ForecastV2> forecasts = new ArrayList<ForecastV2>();

        // The day starts off sunny, but just for 30 minutes
        final WeatherType currentWeather = WeatherType.CLEAR;
        final DateTime sunnyEnd = today9am.plus(Period.minutes(30));
        forecasts.add(new ForecastV2(new Interval(today9am, sunnyEnd), new WeatherWrapperV2(currentWeather)));

        // It gets cloudy for most of the morning
        final DateTime cloudyStart = sunnyEnd;
        final DateTime cloudyEnd = cloudyStart.plus(Period.hours(2));
        forecasts.add(new ForecastV2(new Interval(cloudyStart, cloudyEnd), new WeatherWrapperV2(WeatherType.CLOUDY)));

        // And then rains for hours
        final DateTime rainStart = today9am.plus(Period.minutes(30)).plus(Period.hours(2));
        final DateTime rainEnd = rainStart.plus(Period.hours(10));
        forecasts.add(new ForecastV2(new Interval(rainStart, rainEnd), new WeatherWrapperV2(WeatherType.RAIN)));

        // Compute the day summary
        final ForecastTableV2 forecastTable = new ForecastTableV2(forecasts);
        DaySummaryV2 daySummary = DaySummaryV2.fromForecastTable(forecastTable);

        // Check the results
        Assert.assertEquals(daySummary.getWeatherType(DayPeriod.morning, WeatherPriority.primary), WeatherType.CLOUDY);
        Assert.assertEquals(daySummary.getWeatherType(DayPeriod.morning, WeatherPriority.secondary), WeatherType.RAIN);
        Assert.assertEquals(daySummary.getWeatherType(DayPeriod.afternoon, WeatherPriority.primary), WeatherType.RAIN);
        Assert.assertEquals(daySummary.getWeatherType(DayPeriod.afternoon, WeatherPriority.secondary), WeatherType.UNDEFINED);
    }
}
