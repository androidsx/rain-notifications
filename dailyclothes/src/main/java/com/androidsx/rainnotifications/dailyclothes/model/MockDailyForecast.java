package com.androidsx.rainnotifications.dailyclothes.model;

import com.androidsx.rainnotifications.dailyclothes.R;

import java.util.ArrayList;
import java.util.List;

public class MockDailyForecast {
    public int iconRes;
    public String day;
    public int minTemperature;
    public int maxTemperature;

    public MockDailyForecast(int iconRes, String day, int minTemperature, int maxTemperature) {
        this.iconRes = iconRes;
        this.day = day;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }

    public static List<MockDailyForecast> getMockList() {
        ArrayList<MockDailyForecast> list = new ArrayList<MockDailyForecast>();

        list.add(new MockDailyForecast(R.drawable.ic_rain, "MONDAY", 52, 68));
        list.add(new MockDailyForecast(R.drawable.ic_rain, "TUESDAY", 51, 66));
        list.add(new MockDailyForecast(R.drawable.ic_clear, "WEDNESDAY", 49, 64));
        list.add(new MockDailyForecast(R.drawable.ic_clear, "THURSDAY", 50, 61));
        list.add(new MockDailyForecast(R.drawable.ic_partly_cloudy, "FRIDAY", 48, 60));

        return list;
    }
}
