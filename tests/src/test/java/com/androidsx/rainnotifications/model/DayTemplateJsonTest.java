package com.androidsx.rainnotifications.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Tests for the generation of the day summary, {@link com.androidsx.rainnotifications.alert.DayTemplateGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DayTemplateJsonTest {

    private List<String> assets = Arrays.asList(
            MultiDayTemplateLoader.FROM_MORNING_TEMPLATES_JSON_ASSET,
            MultiDayTemplateLoader.FROM_AFTERNOON_TEMPLATES_JSON_ASSET,
            MultiDayTemplateLoader.FROM_EVENING_TEMPLATES_JSON_ASSET);

    private List<String> templateWeatherValues;
    private String templateFileSource;
    private int failsOnMessages = 0;

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;

        templateWeatherValues = new ArrayList<String>();
        for (WeatherType type : WeatherType.getMeaningfulWeatherTypes()) {
            templateWeatherValues.add(type.toString());
        }
        for (DayTemplate.DayTemplateJokerType joker : DayTemplate.DayTemplateJokerType.values()) {
            templateWeatherValues.add(joker.toString());
        }
    }

    @Test
    public void testSyntactic() throws Exception{
        for (String asset : assets) {
            InputStream is = new FileInputStream("../alert-generator/src/main/assets/" + asset);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            JSONArray dayTemplates =  new JSONArray(new String(buffer, "UTF-8"));

            for (int i = 0 ; i < dayTemplates.length() ; i++) {
                JSONObject dayTemplate = dayTemplates.getJSONObject(i);
                templateFileSource = "on file " + asset + ", DayTemplate number " + i + "\n" + dayTemplate.toString();
                checkDayTemplate(dayTemplate);
            }
        }
    }

    private void checkDayTemplate(JSONObject dayTemplate) throws JSONException {
        Iterator<String> keyIterator = dayTemplate.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("morning") || key.equals("afternoon") || key.equals("evening")) {
                checkDayPeriod(dayTemplate.getJSONObject(key));
            }
            else if(key.equals("messages")) {
                checkMessages(dayTemplate.getJSONObject(key));
            }
            else {
                Assert.fail("Key " + key + " is not a DayPeriod or Messages " + templateFileSource);
            }
        }
    }

    private void checkDayPeriod(JSONObject dayPeriod) throws JSONException {
        Iterator<String> keyIterator = dayPeriod.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("primary") || key.equals("secondary")) {
                checkWeather(dayPeriod.getJSONObject(key));
            }
            else {
                Assert.fail("Key " + key + " is not a WeatherPriority " + templateFileSource);
            }
        }
    }

    private void checkWeather(JSONObject weather) throws JSONException {
        Assert.assertTrue("Not weatherType key " + templateFileSource, weather.has("weatherType"));
        Iterator<String> keyIterator = weather.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("weatherType")) {
                String weatherType = weather.getString(key);
                if(!templateWeatherValues.contains(weatherType)) {
                    Assert.fail("Unknown weatherType: " + weatherType + " " + templateFileSource);
                }
            }
            else {
                Assert.fail("Key " + key + " is not weatherType " + templateFileSource);
            }
        }
    }

    private void checkMessages(JSONObject messages) throws JSONException {
        Assert.assertTrue("Not en key " + templateFileSource, messages.has("en"));
        Iterator<String> keyIterator = messages.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("en")) {
                Assert.assertTrue("Empty messages array " + templateFileSource, messages.getJSONArray(key).length() != 0);

            }
            else {
                Assert.fail("New language " + templateFileSource);
            }
        }
    }

    @Test
    public void testSemantic() {
        for (String asset : assets) {
            List<DayTemplate> templates = JsonDayTemplateLoader.fromFile(new File("../alert-generator/src/main/assets/" + asset)).load();

            for (int i = 0 ; i < templates.size() ; i++) {
                DayTemplate template = templates.get(i);
                templateFileSource = "on file " + asset + ", DayTemplate number " + i + "\n" + template.toString();
                checkWeathers(template);
                checkResolveMessages(template);
            }
        }

        Assert.assertTrue("There are " + failsOnMessages + " fails on messages, see Standard output for details", failsOnMessages == 0);
    }

    private void checkWeathers(DayTemplate template) {
        boolean firstPrimaryFound = false;

        for (DayPeriod period : DayPeriod.values()) {
            Object primaryWeather = template.getWeatherType(period, WeatherPriority.primary);
            Object secondaryWeather = template.getWeatherType(period, WeatherPriority.secondary);

            if(primaryWeather == null) {
                Assert.assertTrue("Found secondary without primary (" + period + ") " + templateFileSource, secondaryWeather == null);
                if(firstPrimaryFound) Assert.fail("Found gap between primary periods " + templateFileSource);
            }
            else {
                if(firstPrimaryFound) {
                    if(primaryWeather instanceof DayTemplate.DayTemplateJokerType && primaryWeather.equals(DayTemplate.DayTemplateJokerType.WHATEVER)) {
                        Assert.fail("Found " + primaryWeather + " on a primary " + templateFileSource);
                    }
                }
                else {
                    firstPrimaryFound = true;
                    if(primaryWeather instanceof DayTemplate.DayTemplateJokerType && !primaryWeather.equals(DayTemplate.DayTemplateJokerType.OTHER)) {
                        Assert.fail("Found " + primaryWeather + " as first primary " + templateFileSource);
                    }
                }

                if(secondaryWeather != null && secondaryWeather instanceof DayTemplate.DayTemplateJokerType && !secondaryWeather.equals(DayTemplate.DayTemplateJokerType.WHATEVER)) {
                    Assert.fail("Found " + secondaryWeather + " as secondary " + templateFileSource);
                }
            }
        }

        if(!firstPrimaryFound) {
            Assert.fail("No weathers found " + templateFileSource);
        }
    }

    private void checkResolveMessages(DayTemplate template) {
        List<String> messages = template.getMessages();

        if(messages == null || messages.isEmpty()) {
            System.out.println("No messages found " + templateFileSource);
            failsOnMessages++;
        }
        else {
            for (String message : messages) {
                String resolvedMessage = message;
                for (DayPeriod period : DayPeriod.values()) {
                    for (WeatherPriority priority : WeatherPriority.values()) {
                        if(template.getWeatherType(period, priority) != null) {
                            resolvedMessage = template.replaceWeatherType(resolvedMessage, period, priority, "OK", "OK", "OK");
                        }
                    }
                }

                if(resolvedMessage.contains("$") || resolvedMessage.contains("{") || resolvedMessage.contains("}") || resolvedMessage.contains("_")) {
                    System.out.println("Has not been able to successfully resolve the message: " + message + " -> " +  resolvedMessage + "\n " + templateFileSource);
                    failsOnMessages++;
                }
                else if(Character.isLowerCase(resolvedMessage.charAt(0))) {
                    System.out.println("Message start with lower case " + templateFileSource);
                    failsOnMessages++;
                }
            }
        }
    }
}
