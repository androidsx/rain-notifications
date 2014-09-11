package com.androidsx.rainnotifications.alert.util;

import android.content.res.Resources;

import com.androidsx.rainnotifications.alert.R;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ResourcesHelper {

    public static String resourceToToRandomAlertMessage(Resources resources, int arrayResource, Random random) {
        return pickRandom(Arrays.asList(resources.getStringArray(arrayResource)), random);
    }

    public static String resourceToToRandomAlertMessage(Resources resources, int arrayResource, Random random, Period periodFromNow) {
        final Locale locale = Locale.getDefault(); // TODO: use the real one
        return String.format(resourceToToRandomAlertMessage(resources, arrayResource, random), periodToString(
                periodFromNow,
                resources.getString(R.string.unit_hours),
                resources.getString(R.string.unit_minutes),
                locale));
    }

    /** Visibility raised from private for testing purposes. */
    static String periodToString(Period period, String hours, String minutes, Locale locale) {
        final PeriodFormatter durationFormatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSeparatorIfFieldsBefore(" " + hours + " and ")
                .appendMinutes()
                .appendSeparatorIfFieldsBefore(" " + minutes)
                .toFormatter()
                .withLocale(locale);

        return durationFormatter.print(period);
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }
}
