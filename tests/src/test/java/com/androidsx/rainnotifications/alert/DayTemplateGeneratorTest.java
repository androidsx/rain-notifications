package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.JsonDayTemplateLoader;
import com.androidsx.rainnotifications.model.MultiDayTemplateLoader;
import com.androidsx.rainnotifications.model.WeatherPriority;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.WeatherWrapper;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
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
 * Tests for the generation of the day summary, {@link com.androidsx.rainnotifications.alert.DayTemplateGenerator}.
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
    }

    private void startTest(String title, String fileName) {
        generator = new DayTemplateGenerator(JsonDayTemplateLoader.fromFile(new File("../alert-generator/src/main/assets/" + fileName)));
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
        Assert.assertTrue(testNonMatches == 0);
    }

    @Test
    public void testFromMorningOnlyPrimary() {
        startTest("From Morning, only primaries:", MultiDayTemplateLoader.FROM_MORNING_TEMPLATES_JSON_ASSET);
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
    public void testFromMorningWithOneSecondary() {
        startTest("From Morning with one secondary:", MultiDayTemplateLoader.FROM_MORNING_TEMPLATES_JSON_ASSET);
        for (WeatherType morningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
            for (WeatherType afternoonPrimary : WeatherType.getMeaningfulWeatherTypes()) {
                for (WeatherType eveningPrimary : WeatherType.getMeaningfulWeatherTypes()) {

                    for (WeatherType morningSecondary : getMoreRelevantWeatherTypes(morningPrimary)) {
                        Day mockDay = getMockDay(morningPrimary, morningSecondary, afternoonPrimary, null, eveningPrimary, null);
                        if(generator.getDayTemplate(mockDay) != null) {
                            addMatch();
                        }
                        else {
                            addNonMatch();
                        }
                    }

                    for (WeatherType afternoonSecondary : getMoreRelevantWeatherTypes(afternoonPrimary)) {
                        Day mockDay = getMockDay(morningPrimary, null, afternoonPrimary, afternoonSecondary, eveningPrimary, null);
                        if(generator.getDayTemplate(mockDay) != null) {
                            addMatch();
                        }
                        else {
                            addNonMatch();
                        }
                    }

                    for (WeatherType eveningSecondary : getMoreRelevantWeatherTypes(eveningPrimary)) {
                        Day mockDay = getMockDay(morningPrimary, null, afternoonPrimary, null, eveningPrimary, eveningSecondary);
                        if(generator.getDayTemplate(mockDay) != null) {
                            addMatch();
                        }
                        else {
                            addNonMatch();
                        }
                    }
                }
            }
        }
        endTest();
    }

    @Test
    public void testFromMorningWithTwoSecondaries() {
        startTest("From Morning with two secondaries:", MultiDayTemplateLoader.FROM_MORNING_TEMPLATES_JSON_ASSET);
        for (WeatherType morningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
            for (WeatherType afternoonPrimary : WeatherType.getMeaningfulWeatherTypes()) {
                for (WeatherType eveningPrimary : WeatherType.getMeaningfulWeatherTypes()) {

                    for (WeatherType morningSecondary : getMoreRelevantWeatherTypes(morningPrimary)) {
                        for (WeatherType afternoonSecondary : getMoreRelevantWeatherTypes(afternoonPrimary)) {
                            Day mockDay = getMockDay(morningPrimary, morningSecondary, afternoonPrimary, afternoonSecondary, eveningPrimary, null);
                            if(generator.getDayTemplate(mockDay) != null) {
                                addMatch();
                            }
                            else {
                                addNonMatch();
                            }
                        }
                    }

                    for (WeatherType morningSecondary : getMoreRelevantWeatherTypes(morningPrimary)) {
                        for (WeatherType eveningSecondary : getMoreRelevantWeatherTypes(eveningPrimary)) {
                            Day mockDay = getMockDay(morningPrimary, morningSecondary, afternoonPrimary, null, eveningPrimary, eveningSecondary);
                            if(generator.getDayTemplate(mockDay) != null) {
                                addMatch();
                            }
                            else {
                                addNonMatch();
                            }
                        }
                    }

                    for (WeatherType afternoonSecondary : getMoreRelevantWeatherTypes(afternoonPrimary)) {
                        for (WeatherType eveningSecondary : getMoreRelevantWeatherTypes(eveningPrimary)) {
                            Day mockDay = getMockDay(morningPrimary, null, afternoonPrimary, afternoonSecondary, eveningPrimary, eveningSecondary);
                            if(generator.getDayTemplate(mockDay) != null) {
                                addMatch();
                            }
                            else {
                                addNonMatch();
                            }
                        }
                    }
                }
            }
        }
        endTest();
    }

    @Test
    public void testFromMorningWithThreeSecondaries() {
        startTest("From Morning with three secondaries:", MultiDayTemplateLoader.FROM_MORNING_TEMPLATES_JSON_ASSET);
        for (WeatherType morningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
            for (WeatherType afternoonPrimary : WeatherType.getMeaningfulWeatherTypes()) {
                for (WeatherType eveningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
                    for (WeatherType morningSecondary : getMoreRelevantWeatherTypes(morningPrimary)) {
                        for (WeatherType afternoonSecondary : getMoreRelevantWeatherTypes(afternoonPrimary)) {
                            for (WeatherType eveningSecondary : getMoreRelevantWeatherTypes(eveningPrimary)) {
                                Day mockDay = getMockDay(morningPrimary, morningSecondary, afternoonPrimary, afternoonSecondary, eveningPrimary, eveningSecondary);
                                if(generator.getDayTemplate(mockDay) != null) {
                                    addMatch();
                                }
                                else {
                                    addNonMatch();
                                }
                            }
                        }
                    }
                }
            }
        }
        endTest();
    }

    @Test
    public void testFromAfternoonOnlyPrimary() {
        startTest("From Afternoon, only primaries:", MultiDayTemplateLoader.FROM_AFTERNOON_TEMPLATES_JSON_ASSET);
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
    public void testFromAfternoonWithOneSecondary() {
        startTest("From Afternoon with one secondary:", MultiDayTemplateLoader.FROM_AFTERNOON_TEMPLATES_JSON_ASSET);
        for (WeatherType afternoonPrimary : WeatherType.getMeaningfulWeatherTypes()) {
            for (WeatherType eveningPrimary : WeatherType.getMeaningfulWeatherTypes()) {

                for (WeatherType afternoonSecondary : getMoreRelevantWeatherTypes(afternoonPrimary)) {
                    Day mockDay = getMockDay(null, null, afternoonPrimary, afternoonSecondary, eveningPrimary, null);
                    if(generator.getDayTemplate(mockDay) != null) {
                        addMatch();
                    }
                    else {
                        addNonMatch();
                    }
                }

                for (WeatherType eveningSecondary : getMoreRelevantWeatherTypes(eveningPrimary)) {
                    Day mockDay = getMockDay(null, null, afternoonPrimary, null, eveningPrimary, eveningSecondary);
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
    public void testFromAfternoonWithTwoSecondaries() {
        startTest("From Afternoon with two secondaries:", MultiDayTemplateLoader.FROM_AFTERNOON_TEMPLATES_JSON_ASSET);
        for (WeatherType afternoonPrimary : WeatherType.getMeaningfulWeatherTypes()) {
            for (WeatherType eveningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
                for (WeatherType afternoonSecondary : getMoreRelevantWeatherTypes(afternoonPrimary)) {
                    for (WeatherType eveningSecondary : getMoreRelevantWeatherTypes(eveningPrimary)) {
                        Day mockDay = getMockDay(null, null, afternoonPrimary, afternoonSecondary, eveningPrimary, eveningSecondary);
                        if(generator.getDayTemplate(mockDay) != null) {
                            addMatch();
                        }
                        else {
                            addNonMatch();
                        }
                    }
                }
            }
        }
        endTest();
    }

    @Test
    public void testFromEveningOnlyPrimary() {
        startTest("From Evening, only primaries:", MultiDayTemplateLoader.FROM_EVENING_TEMPLATES_JSON_ASSET);
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

    @Test
    public void testFromEveningWithSecondary() {
        startTest("From Evening with secondary:", MultiDayTemplateLoader.FROM_EVENING_TEMPLATES_JSON_ASSET);
        for (WeatherType eveningPrimary : WeatherType.getMeaningfulWeatherTypes()) {
            for (WeatherType eveningSecondary : getMoreRelevantWeatherTypes(eveningPrimary)) {
                Day mockDay = getMockDay(null, null, null, null, eveningPrimary, eveningSecondary);
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

    private List<WeatherType> getMoreRelevantWeatherTypes(WeatherType type) {
        List<WeatherType> moreRelevant = new ArrayList<WeatherType>();
        for (WeatherType meaningful : WeatherType.getMeaningfulWeatherTypes()) {
            if(meaningful.getRelevance() > type.getRelevance()) {
                moreRelevant.add(meaningful);
            }
        }
        return moreRelevant;
    }

    private Day getMockDay(WeatherType morningPrimary, WeatherType morningSecondary,
                           WeatherType afternoonPrimary, WeatherType afternoonSecondary,
                           WeatherType eveningPrimary, WeatherType eveningSecondary) {

        List<Forecast> forecastList = new ArrayList<Forecast>();
        DateTime now = new DateTime();

        if(morningPrimary != null) {
            if(morningSecondary != null) {
                Interval morningInterval = DayPeriod.MORNING.getInterval(now);
                Interval morningPrimaryInterval = new Interval(morningInterval.getStart(), morningInterval.getEnd().minusHours(1));
                Interval morningSecondaryInterval = new Interval(morningInterval.getEnd().minusHours(1), morningInterval.getEnd());
                forecastList.add(new Forecast(morningPrimaryInterval, new WeatherWrapper(morningPrimary, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));
                forecastList.add(new Forecast(morningSecondaryInterval, new WeatherWrapper(morningSecondary, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));
            }
            else {
                forecastList.add(new Forecast(DayPeriod.MORNING.getInterval(now), new WeatherWrapper(morningPrimary, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));
            }
        }

        if(afternoonPrimary != null) {
            if(afternoonSecondary != null) {
                Interval afternoonInterval = DayPeriod.AFTERNOON.getInterval(now);
                Interval afternoonPrimaryInterval = new Interval(afternoonInterval.getStart(), afternoonInterval.getEnd().minusHours(1));
                Interval afternoonSecondaryInterval = new Interval(afternoonInterval.getEnd().minusHours(1), afternoonInterval.getEnd());
                forecastList.add(new Forecast(afternoonPrimaryInterval, new WeatherWrapper(afternoonPrimary, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));
                forecastList.add(new Forecast(afternoonSecondaryInterval, new WeatherWrapper(afternoonSecondary, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));
            }
            else {
                forecastList.add(new Forecast(DayPeriod.AFTERNOON.getInterval(now), new WeatherWrapper(afternoonPrimary, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));
            }
        }

        if(eveningPrimary != null) {
            if(eveningSecondary != null) {
                Interval eveningInterval = DayPeriod.EVENING.getInterval(now);
                Interval eveningPrimaryInterval = new Interval(eveningInterval.getStart(), eveningInterval.getEnd().minusHours(1));
                Interval eveningSecondaryInterval = new Interval(eveningInterval.getEnd().minusHours(1), eveningInterval.getEnd());
                forecastList.add(new Forecast(eveningPrimaryInterval, new WeatherWrapper(eveningPrimary, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));
                forecastList.add(new Forecast(eveningSecondaryInterval, new WeatherWrapper(eveningSecondary, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));
            }
            else {
                forecastList.add(new Forecast(DayPeriod.EVENING.getInterval(now), new WeatherWrapper(eveningPrimary, 0f, WeatherWrapper.TemperatureScale.CELSIUS)));
            }
        }

        return new Day(ForecastTable.fromForecastList(forecastList));
    }

    private String getSingleLineDaySummary(Day day) {
        return day.getWeatherType(DayPeriod.MORNING, WeatherPriority.primary) + "_"
                + day.getWeatherType(DayPeriod.MORNING, WeatherPriority.secondary) + "_"
                + day.getWeatherType(DayPeriod.AFTERNOON, WeatherPriority.primary) + "_"
                + day.getWeatherType(DayPeriod.AFTERNOON, WeatherPriority.secondary) + "_"
                + day.getWeatherType(DayPeriod.EVENING, WeatherPriority.primary) + "_"
                + day.getWeatherType(DayPeriod.EVENING, WeatherPriority.secondary);
    }
}
