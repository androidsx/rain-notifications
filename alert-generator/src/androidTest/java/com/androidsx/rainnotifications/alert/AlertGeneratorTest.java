package com.androidsx.rainnotifications.alert;

import android.test.InstrumentationTestCase;

import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Weather;

import static com.androidsx.rainnotifications.model.WeatherType.*;

import junit.framework.Assert;

public class AlertGeneratorTest extends InstrumentationTestCase {
    private AlertGenerator generator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        generator = new AlertGenerator(null);
    }

    public void testFromRainToNonRainNotifies() {
        Assert.assertEquals(AlertLevel.INFO, generator.generateAlertLevel(new Weather(RAIN), new Weather(UNKNOWN)));
    }

    public void testFromNonRainToRainNotifies() {
        Assert.assertEquals(AlertLevel.INFO, generator.generateAlertLevel(new Weather(UNKNOWN), new Weather(RAIN)));
    }

    public void testFromRainTRainNeverMind() {
        Assert.assertEquals(AlertLevel.NEVER_MIND, generator.generateAlertLevel(new Weather(RAIN), new Weather(RAIN)));
    }

    public void testFromNonRainToNonRainNeverMind() {
        Assert.assertEquals(AlertLevel.NEVER_MIND, generator.generateAlertLevel(new Weather(UNKNOWN), new Weather(UNKNOWN)));
    }
}
