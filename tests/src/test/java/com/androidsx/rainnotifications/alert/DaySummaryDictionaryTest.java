package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.DaySummary;
import com.androidsx.rainnotifications.model.DaySummaryDeserializer;
import com.androidsx.rainnotifications.model.WeatherPriority;
import com.androidsx.rainnotifications.model.WeatherType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the generation of the day summary, {@link DaySummaryGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DaySummaryDictionaryTest {

    private List<DaySummary> dictionary;
    private List<WeatherType> summaryPossibleTypes;

    @Before
    public void setUp() throws Exception {
        dictionary = DaySummaryDeserializer.deserializeDaySummaryDictionary(new InputStreamReader(new FileInputStream("../alert-generator/src/main/assets/dayMessages.json")));
        summaryPossibleTypes = new ArrayList<WeatherType>(WeatherType.getMeaningfulWeatherTypes());
        summaryPossibleTypes.add(WeatherType.UNDEFINED);

        ShadowLog.stream = System.out;
    }

    @Test
    public void testCompletenessStatistics() {
        final DaySummaryGenerator generator = new DaySummaryGenerator(dictionary);

        DomainRunnable runnable = new DomainRunnable() {
            private int matches = 0;
            private int downgradedMatches = 0;
            private int noMatches = 0;

            @Override
            public void run(DaySummary daySummary) {
                if(generator.getPostProcessor().getDaySummaryFromMap(daySummary) != null) {
                    matches++;
                }
                else if (!generator.getPostProcessor().getClosestDaySummary(daySummary).getDayMessage().equals(DaySummary.DEFAULT_MESSAGE)) {
                    downgradedMatches++;
                }
                else {
                    noMatches++;
                }
            }

            @Override
            public String getResult() {
                StringBuilder builder = new StringBuilder();
                builder.append("Matches: " + matches);
                builder.append("\ndowngradedMatches: " + downgradedMatches);
                builder.append("\nnoMatches: " + noMatches);
                return builder.toString();
            }
        };

        doOnDomain(runnable);
        System.out.print(runnable.getResult());
    }

    private void doOnDomain(DomainRunnable runnable) {
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

                                        runnable.run(daySummary);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private interface DomainRunnable {
        public void run(DaySummary daySummary);
        public String getResult();
    }
}
