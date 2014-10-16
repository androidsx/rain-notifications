package com.androidsx.rainnotifications.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tests for the generation of the day summary, {@link com.androidsx.rainnotifications.alert.DayTemplateGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DayTemplateDomainTest {

    /**
     * If this test fails is due to modifications on {@link com.androidsx.rainnotifications.model.DayPeriod}, therefore is
     * necessary to modify the Test {@link com.androidsx.rainnotifications.alert.DayTemplateGeneratorTest}
     */
    @Test
    public void testDayPeriodDomain() {
        ArrayList<DayPeriod> dayPeriods = new ArrayList<DayPeriod>(Arrays.asList(DayPeriod.values()));
        dayPeriods.remove(DayPeriod.morning);
        dayPeriods.remove(DayPeriod.afternoon);
        dayPeriods.remove(DayPeriod.evening);
        Assert.assertTrue(dayPeriods.isEmpty());
    }

    /**
     * If this test fails is due to modifications on {@link com.androidsx.rainnotifications.model.WeatherPriority}, therefore is
     * necessary to modify the Test {@link com.androidsx.rainnotifications.alert.DayTemplateGeneratorTest}
     */
    @Test
    public void testWeatherPriorityDomain() {
        ArrayList<WeatherPriority> weatherPriorities = new ArrayList<WeatherPriority>(Arrays.asList(WeatherPriority.values()));
        weatherPriorities.remove(WeatherPriority.primary);
        weatherPriorities.remove(WeatherPriority.secondary);
        Assert.assertTrue(weatherPriorities.isEmpty());
    }
}
