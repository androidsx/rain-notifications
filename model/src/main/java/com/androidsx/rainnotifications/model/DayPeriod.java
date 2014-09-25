package com.androidsx.rainnotifications.model;

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
}
