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
    private String templateTrace;

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
                templateTrace = "on file " + asset + ", DayTemplate number " + i + "\n" + dayTemplate.toString();
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
                Assert.fail("Key " + key + " is not a DayPeriod or Messages " + templateTrace);
            }
        }
    }

    private void checkDayPeriod(JSONObject dayPeriod) throws JSONException {
        Assert.assertTrue("Not primary key " + templateTrace, dayPeriod.has("primary"));
        Iterator<String> keyIterator = dayPeriod.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("primary") || key.equals("secondary")) {
                checkWeather(dayPeriod.getJSONObject(key));
            }
            else {
                Assert.fail("Key " + key + " is not a WeatherPriority " + templateTrace);
            }
        }
    }

    private void checkWeather(JSONObject weather) throws JSONException {
        Assert.assertTrue("Not weatherType key " + templateTrace, weather.has("weatherType"));
        Iterator<String> keyIterator = weather.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("weatherType")) {
                String weatherType = weather.getString(key);
                if(!templateWeatherValues.contains(weatherType)) {
                    Assert.fail("Unknown weatherType: " + weatherType + " " + templateTrace);
                }
            }
            else {
                Assert.fail("Key " + key + " is not weatherType " + templateTrace);
            }
        }
    }

    private void checkMessages(JSONObject messages) throws JSONException {
        Assert.assertTrue("Not en key " + templateTrace, messages.has("en"));
        Iterator<String> keyIterator = messages.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if(key.equals("en")) {
                Assert.assertTrue("Empty messages array " + templateTrace, messages.getJSONArray(key).length() != 0);

            }
            else {
                Assert.fail("New language " + templateTrace);
            }
        }
    }

    @Test
    public void testSemantic() {
        for (String asset : assets) {
            List<DayTemplate> templates = JsonDayTemplateLoader.fromFile(new File("../alert-generator/src/main/assets/" + asset)).load();

            for (DayTemplate template : templates) {
                checkWeathers(template);
                checkResolveMessages(template);
            }
        }
    }

    private void checkWeathers(DayTemplate template) {



    }

    private void checkResolveMessages(DayTemplate template) {

    }
}
