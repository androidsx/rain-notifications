package com.androidsx.rainnotifications.model.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * A bunch of utility methods.
 * <p/>
 * This should really be moved to a different project, but there is so little logic that we may as
 * well keep it here for now.
 */
public class UiUtil {

    private UiUtil() {
        // Non-instantiable
    }

    public static String getReadableHour(DateTime dateTime) {
        return DateTimeFormat.forPattern("h aa").print(dateTime);
    }

    public static PeriodFormatter getDebugOnlyPeriodFormatter() {
        return new PeriodFormatterBuilder()
                .appendDays()
                .appendSeparatorIfFieldsBefore(" days ")
                .appendHours()
                .appendSeparatorIfFieldsBefore(" hours ")
                .appendMinutes()
                .appendSeparatorIfFieldsBefore(" minutes ")
                .appendSeconds()
                .appendSeparatorIfFieldsBefore(" seconds")
                .toFormatter();
    }
}
