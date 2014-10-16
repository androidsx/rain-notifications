package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.JsonDayTemplateLoader;
import com.androidsx.rainnotifications.model.WeatherPriority;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.WeatherWrapper;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the generation of the day summary, {@link DayTemplateGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DayTemplateGeneratorTest {

    private DayTemplateGenerator generator;
    private String testTitle;
    private int testMatches;
    private int testNonMatches;

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;
        generator = new DayTemplateGenerator(JsonDayTemplateLoader.fromFile(new File("../alert-generator/src/main/assets/dayTemplates.json")));
    }

    private void startTest(String title) {
        testTitle = title;
        testMatches = 0;
        testNonMatches = 0;
    }

    private void addMatch() {
        testMatches++;
    }

    private void addNonMatch() {
        testNonMatches++;
    }

    private void endTest() {
        System.out.println(testTitle);
        System.out.println("     matches: " + testMatches);
        System.out.println("     no matches: " + testNonMatches);
        //TODO: Remove this line when test are all implemented
        //Assert.assertTrue(testNonMatches == 0);
    }

    @Test
    public void testAllDayOnlyPrimary() {
        startTest("All Day, only primaries:");
        for (WeatherType morningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
            for (WeatherType afternoonPrimary : WeatherType.getMeaningfulWeatherTypes()) {
                for (WeatherType eveningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
                    Day mockDay = getMockDay(morningPrimary, null, afternoonPrimary, null, eveningPrimary, null);
                    if(generator.getDayTemplate(mockDay) != null) {
                        addMatch();
                    }
                    else {
                        addNonMatch();
                    }
                }
            }
        }
        endTest();
    }

    @Test
    public void testFromAfternoonOnlyPrimary() {
        startTest("From Afternoon, only primaries:");
        for (WeatherType afternoonPrimary : WeatherType.getMeaningfulWeatherTypes()) {
            for (WeatherType eveningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
                Day mockDay = getMockDay(null, null, afternoonPrimary, null, eveningPrimary, null);
                if(generator.getDayTemplate(mockDay) != null) {
                    addMatch();
                }
                else {
                    addNonMatch();
                }
            }
        }
        endTest();
    }

    @Test
    public void testFromEveningOnlyPrimary() {
        startTest("From Evening, only primaries:");
        for (WeatherType eveningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
            Day mockDay = getMockDay(null, null, null, null, eveningPrimary, null);
            if(generator.getDayTemplate(mockDay) != null) {
                addMatch();
            }
            else {
                addNonMatch();
            }
        }
        endTest();
    }


    private Day getMockDay(WeatherType morningPrimary, WeatherType morningSecondary,
                           WeatherType afternoonPrimary, WeatherType afternoonSecondary,
                           WeatherType eveningPrimary, WeatherType eveningSecondary) {

        List<Forecast> forecastList = new ArrayList<Forecast>();
        DateTime now = new DateTime();

        if(morningPrimary != null) {
            if(morningSecondary != null) {
                Interval morningInterval = DayPeriod.morning.getInterval(now);
                Interval morningPrimaryInterval = new Interval(morningInterval.getStart(), morningInterval.getEnd().minusHours(1));
                Interval morningSecondaryInterval = new Interval(morningInterval.getEnd().minusHours(1), morningInterval.getEnd());
                forecastList.add(new Forecast(morningPrimaryInterval, new WeatherWrapper(morningPrimary)));
                forecastList.add(new Forecast(morningSecondaryInterval, new WeatherWrapper(morningSecondary)));
            }
            else {
                forecastList.add(new Forecast(DayPeriod.morning.getInterval(now), new WeatherWrapper(morningPrimary)));
            }
        }

        if(afternoonPrimary != null) {
            if(afternoonSecondary != null) {
                Interval afternoonInterval = DayPeriod.afternoon.getInterval(now);
                Interval afternoonPrimaryInterval = new Interval(afternoonInterval.getStart(), afternoonInterval.getEnd().minusHours(1));
                Interval afternoonSecondaryInterval = new Interval(afternoonInterval.getEnd().minusHours(1), afternoonInterval.getEnd());
                forecastList.add(new Forecast(afternoonPrimaryInterval, new WeatherWrapper(afternoonPrimary)));
                forecastList.add(new Forecast(afternoonSecondaryInterval, new WeatherWrapper(afternoonSecondary)));
            }
            else {
                forecastList.add(new Forecast(DayPeriod.afternoon.getInterval(now), new WeatherWrapper(afternoonPrimary)));
            }
        }

        if(eveningPrimary != null) {
            if(eveningSecondary != null) {
                Interval eveningInterval = DayPeriod.evening.getInterval(now);
                Interval eveningPrimaryInterval = new Interval(eveningInterval.getStart(), eveningInterval.getEnd().minusHours(1));
                Interval eveningSecondaryInterval = new Interval(eveningInterval.getEnd().minusHours(1), eveningInterval.getEnd());
                forecastList.add(new Forecast(eveningPrimaryInterval, new WeatherWrapper(eveningPrimary)));
                forecastList.add(new Forecast(eveningSecondaryInterval, new WeatherWrapper(eveningSecondary)));
            }
            else {
                forecastList.add(new Forecast(DayPeriod.evening.getInterval(now), new WeatherWrapper(eveningPrimary)));
            }
        }

        return new Day(ForecastTable.fromForecastList(forecastList));
    }

    private String getSingleLineDaySummary(Day day) {
        return day.getWeatherType(DayPeriod.morning, WeatherPriority.primary) + "_"
                + day.getWeatherType(DayPeriod.morning, WeatherPriority.secondary) + "_"
                + day.getWeatherType(DayPeriod.afternoon, WeatherPriority.primary) + "_"
                + day.getWeatherType(DayPeriod.afternoon, WeatherPriority.secondary) + "_"
                + day.getWeatherType(DayPeriod.evening, WeatherPriority.primary) + "_"
                + day.getWeatherType(DayPeriod.evening, WeatherPriority.secondary);
    }
}
