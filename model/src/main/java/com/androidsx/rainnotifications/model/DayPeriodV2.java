package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public enum DayPeriodV2 {
    morning(7, 12),
    afternoon(12, 18),
    evening(18, 7); // This also cover night.

    private int startHour;
    private int endHour;

    private DayPeriodV2(int startHour, int endHour) {
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
