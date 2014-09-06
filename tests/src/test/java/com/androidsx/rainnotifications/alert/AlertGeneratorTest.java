package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Weather;

import static com.androidsx.rainnotifications.model.WeatherType.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class AlertGeneratorTest {
    private AlertGenerator generator;

    @Before
    public void setUp() {
        generator = new AlertGenerator(null);
    }

    @Test
    public void testFromRainToNonRainNotifies() {
        Assert.assertEquals(AlertLevel.INFO, generator.generateAlertLevel(new Weather(RAIN), new Weather(UNKNOWN)));
    }

    @Test
    public void testFromNonRainToRainNotifies() {
        Assert.assertEquals(AlertLevel.INFO, generator.generateAlertLevel(new Weather(UNKNOWN), new Weather(RAIN)));
    }

    @Test
    public void testFromRainTRainNeverMind() {
        Assert.assertEquals(AlertLevel.NEVER_MIND, generator.generateAlertLevel(new Weather(RAIN), new Weather(RAIN)));
    }

    @Test
    public void testFromNonRainToNonRainNeverMind() {
        Assert.assertEquals(AlertLevel.NEVER_MIND, generator.generateAlertLevel(new Weather(UNKNOWN), new Weather(UNKNOWN)));
    }
}
