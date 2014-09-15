package com.androidsx.rainnotifications.alert;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;

/**
 * Tests for the alert levels within {@link AlertGenerator}.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class AlertGeneratorAlertMessagesTest {
    private AlertGenerator generator;

    @Before
    public void setUp() {
        generator = new AlertGenerator(null);
    }

    @Test
    public void testOnlyMinutesAppearInAlertMessageForEarlyTransition() {
        final DateTime now = new DateTime(2014, 9, 6, 11, 30, 0, 0);
        final DateTime later = now.plus(Period.minutes(20));

        final String periodMessage = generator.periodToString(new Interval(now, later).toPeriod(), "hours", "minutes", Locale.US);

        Assert.assertTrue(periodMessage.contains("20 minutes"));
        Assert.assertFalse(periodMessage.contains("hour"));
    }

    @Test
    public void testHoursAndMinutesAppearInAlertMessage() {
        final DateTime now = new DateTime(2014, 9, 6, 11, 30, 0, 0);
        final DateTime later = now.plus(Period.hours(2).plusMinutes(35));

        final String periodMessage = generator.periodToString(new Interval(now, later).toPeriod(), "hours", "minutes", Locale.US);

        Assert.assertTrue(periodMessage.contains("2 hours"));
        Assert.assertTrue(periodMessage.contains("35 minutes"));
        Assert.assertTrue(periodMessage.contains("2 hours and 35 minutes"));
    }
}
