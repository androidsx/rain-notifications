package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public enum DayPeriod {
    morning(7, 12),
    afternoon(12, 18),
    evening(18, 21),
    night(21, 7);

    private int startHour;
    private int endHour;

    private DayPeriod(int startHour, int endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public Interval getInterval(DateTime baselineTime) {
        if(startHour < endHour) {
            return new Interval(baselineTime.withHourOfDay(startHour).withMinuteOfHour(0).withSecondOfMinute(0), baselineTime.withHourOfDay(endHour).withMinuteOfHour(0).withSecondOfMinute(0));
        }
        else {
            return new Interval(baselineTime.withHourOfDay(startHour).withMinuteOfHour(0).withSecondOfMinute(0), baselineTime.withHourOfDay(endHour).withMinuteOfHour(0).withSecondOfMinute(0).plusDays(1));
        }

    }
}
