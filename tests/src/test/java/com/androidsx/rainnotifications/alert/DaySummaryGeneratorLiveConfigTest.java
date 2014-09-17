package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.DaySummary;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the generation of the day summary, {@link com.androidsx.rainnotifications.alert.DaySummaryGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DaySummaryGeneratorLiveConfigTest {
    private DaySummaryGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new DaySummaryGenerator(null);

        generator.init(new FileInputStream("../alert-generator/src/main/assets/dayMessages.json"));
    }

    @Test
    public void testAllSunnyDay() {
        final DateTime today9am = new DateTime(2014, 9, 17, 9, 0);
        final Weather currentWeather = new Weather(WeatherType.CLEAR);
        final List<Forecast> forecasts = new ArrayList<Forecast>();
        forecasts.add(new Forecast(new Weather(WeatherType.CLEAR), new Interval(today9am, today9am.plus(Period.minutes(20))), Forecast.Granularity.HOUR));
        forecasts.add(new Forecast(new Weather(WeatherType.CLEAR), new Interval(today9am, today9am.plus(Period.hours(10))), Forecast.Granularity.HOUR));
        final ForecastTable forecastTable = ForecastTable.create(currentWeather, today9am, null, null, forecasts);

        final DaySummary daySummary = generator.getDaySummary(forecastTable);
        Assert.assertEquals(WeatherType.CLEAR, daySummary.getMorningWeather());
        Assert.assertEquals(WeatherType.CLEAR, daySummary.getAfternoonWeather());
        Assert.assertTrue(daySummary.getDayMessage().contains("sun"));
        Assert.assertFalse(daySummary.getDayMessage().contains("rain"));
    }
}
