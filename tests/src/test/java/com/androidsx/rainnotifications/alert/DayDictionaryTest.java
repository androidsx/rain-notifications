package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.WeatherPriority;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Tests for the generation of the day summary, {@link DayTemplateGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DayDictionaryTest {
    // TODO: Re-Implement and improve this tests when DaySummary V2 is ready.
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
    public void testSyntactic() throws Exception{
        // TODO: ReImplement with new template rules.
        /*
        InputStream is = new FileInputStream("../alert-generator/src/main/assets/dayMessages.json");
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();
        JSONArray daySummaries =  new JSONArray(new String(buffer, "UTF-8"));

        for (int i = 0 ; i < daySummaries.length() ; i++) {
            JSONObject daySummary = daySummaries.getJSONObject(i);
            String errorTrace = "on DaySummary number " + i + "\n" + daySummary.toString();
            checkDaySummary(daySummary, errorTrace);
        }
        */
    }

    private void checkDaySummary(JSONObject daySummary, String errorTrace) throws JSONException {
        Iterator<String> keyIterator = daySummary.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("morning") || key.equals("afternoon") || key.equals("evening") || key.equals("night")) {
                checkDayPeriod(daySummary.getJSONObject(key), errorTrace);
            }
            else if(key.equals("messages")) {
                checkMessages(daySummary.getJSONObject(key), errorTrace);
            }
            else {
                Assert.fail("Key " + key + " is not a DayPeriod or Messages " + errorTrace);
            }
        }
    }

    private void checkDayPeriod(JSONObject dayPeriod, String errorTrace) throws JSONException {
        Assert.assertTrue("Not primary key " + errorTrace, dayPeriod.has("primary"));
        Iterator<String> keyIterator = dayPeriod.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("primary") || key.equals("secondary")) {
                checkWeather(dayPeriod.getJSONObject(key), errorTrace);
            }
            else {
                Assert.fail("Key is not a WeatherPriority " + errorTrace);
            }
        }
    }

    private void checkWeather(JSONObject weather, String errorTrace) throws JSONException {
        Assert.assertTrue("Not weatherType key " + errorTrace, weather.has("weatherType"));
        Iterator<String> keyIterator = weather.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("weatherType")) {
                String value = weather.getString(key);
                if (!(value.equals("CLEAR") || value.equals("RAIN") || value.equals("CLOUDY") || value.equals("PARTLY_CLOUDY") || value.equals("*"))) {
                    Assert.fail("Unknown weatherType " + errorTrace);
                }
            }
            else {
                Assert.fail("Key is not weatherType " + errorTrace);
            }
        }
    }

    private void checkMessages(JSONObject messages, String errorTrace) throws JSONException {
        Assert.assertTrue("Not en key " + errorTrace, messages.has("en"));
        Iterator<String> keyIterator = messages.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("en")) {
                Assert.assertTrue("Empty messages array " + errorTrace, messages.getJSONArray(key).length() != 0);

            }
            else {
                Assert.fail("New language " + errorTrace);
            }
        }
    }

    @Test
    public void testCompletenessStatistics() {
        //TODO: ReImplement

        //DayTemplateGenerator generator = new DayTemplateGenerator(JsonDayTemplateLoader.fromFile(new File("../alert-generator/src/main/assets/dayTemplates.json")));

        /*
        List<Day> dictionary = DaySummaryDeserializer.deserializeDaySummaryDictionary(new InputStreamReader(new FileInputStream("../alert-generator/src/main/assets/dayMessages.json")));
        DayTemplateGenerator generator = new DayTemplateGenerator(dictionary);
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
                                        Day day = (new Day.DaySummaryBuilder()
                                                .setWeatherType(DayPeriod.morning, WeatherPriority.primary, morningPrimary)
                                                .setWeatherType(DayPeriod.morning, WeatherPriority.secondary, morningSecondary)
                                                .setWeatherType(DayPeriod.afternoon, WeatherPriority.primary, afternoonPrimary)
                                                .setWeatherType(DayPeriod.afternoon, WeatherPriority.secondary, afternoonSecondary)
                                                .setWeatherType(DayPeriod.evening, WeatherPriority.primary, eveningPrimary)
                                                .setWeatherType(DayPeriod.evening, WeatherPriority.secondary, eveningSecondary)
                                                .setWeatherType(DayPeriod.night, WeatherPriority.primary, nightPrimary)
                                                .setWeatherType(DayPeriod.night, WeatherPriority.secondary, nightSecondary)
                                                .build());

                                        String daySummarySingleLine = getSingleLineDaySummary(day);

                                        if(generator.getPostProcessor().getDaySummary(day) != null) {
                                            matchesCount++;
                                        }
                                        else if (!generator.getPostProcessor().getClosestDaySummary(day).getDayMessage().equals(Day.DEFAULT_MESSAGE)) {
                                            downgradedMatchesCount++;
                                        }
                                        else {
                                            noMatchesCount++;
                                            System.out.println("NO MATCH FOR: " + daySummarySingleLine);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Total matches: " + matchesCount);
        System.out.println("Total downgraded matches: " + downgradedMatchesCount);
        System.out.println("Total no match: " + noMatchesCount);
        */
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
