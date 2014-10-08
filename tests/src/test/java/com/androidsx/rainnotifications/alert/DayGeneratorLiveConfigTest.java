package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.JsonDayTemplateLoader;
import com.androidsx.rainnotifications.model.WeatherPriority;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.WeatherWrapper;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the generation of the day summary, {@link DayTemplateGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DayGeneratorLiveConfigTest {
    private DayTemplateGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new DayTemplateGenerator(new JsonDayTemplateLoader(new InputStreamReader(new FileInputStream("../alert-generator/src/main/assets/dayTemplates.json"))));
    }

    // TODO: Create more and better test cases

    @Test
    public void testAllSunnyDay() {
        final List<Forecast> forecasts = new ArrayList<Forecast>();
        for (DayPeriod period : DayPeriod.values()) {
            forecasts.add(new Forecast(period.getInterval(new DateTime()), new WeatherWrapper(WeatherType.CLEAR)));
        }

        final Day day = new Day(ForecastTable.fromForecastList(forecasts));

        for (DayPeriod period : DayPeriod.values()) {
            Assert.assertEquals(WeatherType.CLEAR, day.getWeatherType(period, WeatherPriority.primary));
            Assert.assertTrue(day.getWeatherType(period, WeatherPriority.secondary) == null);
        }
        // Assert.assertTrue(daySummary.getDayMessage().contains("sun"));
        // Assert.assertFalse(daySummary.getDayMessage().contains("rain"));
    }
}
