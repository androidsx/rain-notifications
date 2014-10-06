package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.DaySummary;
import com.androidsx.rainnotifications.model.DaySummaryDeserializer;
import com.androidsx.rainnotifications.model.WeatherPriority;
import com.androidsx.rainnotifications.model.WeatherType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for the generation of the day summary, {@link DaySummaryGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DaySummaryDictionaryTest {

    // TODO: Re-Implement and improve this test when DaySummary V2 is ready.

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;
    }

    /**
     * If this test fails is due to modifications on {@link com.androidsx.rainnotifications.model.DayPeriod}, therefore is
     * necessary to modify the method {@link #testCompletenessStatistics()}
     */
    @Test
    public void testDayPeriodDomain() {
        ArrayList<DayPeriod> dayPeriods = new ArrayList<DayPeriod>(Arrays.asList(DayPeriod.values()));
        dayPeriods.remove(DayPeriod.morning);
        dayPeriods.remove(DayPeriod.afternoon);
        dayPeriods.remove(DayPeriod.evening);
        dayPeriods.remove(DayPeriod.night);
        Assert.assertTrue(dayPeriods.isEmpty());
    }

    /**
     * If this test fails is due to modifications on {@link com.androidsx.rainnotifications.model.WeatherPriority}, therefore is
     * necessary to modify the method {@link #testCompletenessStatistics()}
     */
    @Test
    public void testWeatherPriorityDomain() {
        ArrayList<WeatherPriority> weatherPriorities = new ArrayList<WeatherPriority>(Arrays.asList(WeatherPriority.values()));
        weatherPriorities.remove(WeatherPriority.primary);
        weatherPriorities.remove(WeatherPriority.secondary);
        Assert.assertTrue(weatherPriorities.isEmpty());
    }

    @Test
    public void testCompletenessStatistics() throws Exception{
        List<DaySummary> dictionary = DaySummaryDeserializer.deserializeDaySummaryDictionary(new InputStreamReader(new FileInputStream("../alert-generator/src/main/assets/dayMessages.json")));
        DaySummaryGenerator generator = new DaySummaryGenerator(dictionary);
        List<WeatherType> summaryPossibleTypes = new ArrayList<WeatherType>(WeatherType.getMeaningfulWeatherTypes());
        summaryPossibleTypes.add(WeatherType.UNDEFINED);

        int matchesCount = 0;
        int downgradedMatchesCount = 0;
        int noMatchesCount = 0;

        for (WeatherType morningPrimary : summaryPossibleTypes) {
            for (WeatherType morningSecondary : summaryPossibleTypes) {
                for (WeatherType afternoonPrimary : summaryPossibleTypes) {
                    for (WeatherType afternoonSecondary : summaryPossibleTypes) {
                        for (WeatherType eveningPrimary : summaryPossibleTypes) {
                            for (WeatherType eveningSecondary : summaryPossibleTypes) {
                                for (WeatherType nightPrimary : summaryPossibleTypes) {
                                    for (WeatherType nightSecondary : summaryPossibleTypes) {
                                        DaySummary daySummary = (new DaySummary.DaySummaryBuilder()
                                                .setWeatherType(DayPeriod.morning, WeatherPriority.primary, morningPrimary)
                                                .setWeatherType(DayPeriod.morning, WeatherPriority.secondary, morningSecondary)
                                                .setWeatherType(DayPeriod.afternoon, WeatherPriority.primary, afternoonPrimary)
                                                .setWeatherType(DayPeriod.afternoon, WeatherPriority.secondary, afternoonSecondary)
                                                .setWeatherType(DayPeriod.evening, WeatherPriority.primary, eveningPrimary)
                                                .setWeatherType(DayPeriod.evening, WeatherPriority.secondary, eveningSecondary)
                                                .setWeatherType(DayPeriod.night, WeatherPriority.primary, nightPrimary)
                                                .setWeatherType(DayPeriod.night, WeatherPriority.secondary, nightSecondary)
                                                .build());

                                        String daySummarySingleLine = getSingleLineDaySummary(daySummary);

                                        if(generator.getPostProcessor().getDaySummary(daySummary) != null) {
                                            matchesCount++;
                                        }
                                        else if (!generator.getPostProcessor().getClosestDaySummary(daySummary).getDayMessage().equals(DaySummary.DEFAULT_MESSAGE)) {
                                            downgradedMatchesCount++;
                                        }
                                        else {
                                            noMatchesCount++;
                                            System.out.println(daySummarySingleLine);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Matches: " + matchesCount);
        System.out.println("Downgraded matches: " + downgradedMatchesCount);
        System.out.println("No Match: " + noMatchesCount);
    }

    private String getSingleLineDaySummary(DaySummary daySummary) {
        return daySummary.getWeatherType(DayPeriod.morning, WeatherPriority.primary) + "_"
                + daySummary.getWeatherType(DayPeriod.morning, WeatherPriority.secondary) + "_"
                + daySummary.getWeatherType(DayPeriod.afternoon, WeatherPriority.primary) + "_"
                + daySummary.getWeatherType(DayPeriod.afternoon, WeatherPriority.secondary) + "_"
                + daySummary.getWeatherType(DayPeriod.evening, WeatherPriority.primary) + "_"
                + daySummary.getWeatherType(DayPeriod.evening, WeatherPriority.secondary) + "_"
                + daySummary.getWeatherType(DayPeriod.night, WeatherPriority.primary) + "_"
                + daySummary.getWeatherType(DayPeriod.night, WeatherPriority.secondary);
    }
}
