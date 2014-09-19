package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.WeatherTypeMascots;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Tests for the processing of the JSON configuration in {@link AlertGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class AlertGeneratorConfigProcessingTest {

    @Test
    public void testSameWeatherTransition() {
        final AlertGenerator alertGenerator = new AlertGenerator(Robolectric.application);

        // Create the alert (what would be in alerts.json)
        final HashMap<String, List<String>> alertMessages = new HashMap<String, List<String>>();
        alertMessages.put("en", Arrays.asList(new String("will stay clear")));
        final Alert alert = new Alert(WeatherType.CLEAR, WeatherType.CLEAR, AlertLevel.NEVER_MIND, alertMessages, 0);

        // Create the mascots (what would be in weatherTypeMascots.json)
        final WeatherTypeMascots weatherTypeMascots = new WeatherTypeMascots(WeatherType.CLEAR, Arrays.asList("mascot_drawable_id"));

        // Generate the actual alert
        alertGenerator.init(Arrays.asList(new Alert[] {alert}), Arrays.asList(new WeatherTypeMascots[] {weatherTypeMascots}));
        final Weather from = new Weather(WeatherType.CLEAR);
        final Forecast to = new Forecast(new Weather(WeatherType.CLEAR), null, null);
        final Alert generatedAlert = alertGenerator.generateAlert(from, to);

        // Make sure the alert is exactly as we expected
        Assert.assertEquals(alert, generatedAlert);
    }

    // Shall we have our own WrongConfigException?
    @Test(expected = Exception.class)
    public void testTransitionNotDefinedThrowsException() {
        final AlertGenerator alertGenerator = new AlertGenerator(Robolectric.application);

        // Create the alert (what would be in alerts.json)
        final HashMap<String, List<String>> alertMessages = new HashMap<String, List<String>>();
        alertMessages.put("en", Arrays.asList(new String("will stay clear")));
        final Alert alert = new Alert(WeatherType.CLEAR, WeatherType.CLEAR, AlertLevel.NEVER_MIND, alertMessages, 0);

        // Create the mascots (what would be in weatherTypeMascots.json)
        final WeatherTypeMascots weatherTypeMascots = new WeatherTypeMascots(WeatherType.CLEAR, Arrays.asList("mascot_drawable_id"));

        // Generate the actual alert
        alertGenerator.init(Arrays.asList(new Alert[] {alert}), Arrays.asList(new WeatherTypeMascots[] {weatherTypeMascots}));
        final Weather from = new Weather(WeatherType.CLEAR);
        final Forecast to = new Forecast(new Weather(WeatherType.RAIN), null, null);

        alertGenerator.generateAlert(from, to); // This should throw an exception
    }
}
