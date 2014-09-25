package com.androidsx.rainnotifications.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DaySummary {
    private final Random random = new Random();
    private HashMap<DayPeriod, HashMap<WeatherPriority, WeatherWrapper>> weatherMap;
    private HashMap<String, List<String>> messages;

    private DaySummary(DaySummaryBuilder builder) {
        this.weatherMap = builder.weatherMap;
        this.messages = builder.messages;
    }

    public void setWeatherWrapper(DayPeriod period, WeatherPriority priority, WeatherWrapper wrapper) {
        weatherMap.get(period).put(priority, wrapper);
    }

    public WeatherWrapper getWeatherWrapper(DayPeriod period, WeatherPriority priority) {
        return weatherMap.get(period).get(priority);
    }

    public void setMessages(HashMap<String, List<String>> messages) {
        this.messages = messages;
    }

    public String getDayMessage() {
        return pickRandom(messages.get("en"), random);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Day Messages for: ");

        for (DayPeriod period : DayPeriod.values()) {
            for (WeatherPriority priority : WeatherPriority.values()) {
                builder.append("\n" + period + " " + priority + " weather: " + getWeatherWrapper(period, priority));
            }
        }

        if (messages.containsKey("en")) {
            builder.append("\n\nMessages:");
            for (String s : messages.get("en")) {
                builder.append(String.format("Message (en): %s", s) + "\n");
            }
        }
        return builder.toString();
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }

    public static class DaySummaryBuilder {
        private HashMap<DayPeriod, HashMap<WeatherPriority, WeatherWrapper>> weatherMap;
        private HashMap<String, List<String>> messages;

        public DaySummaryBuilder() {
            weatherMap = new HashMap<DayPeriod, HashMap<WeatherPriority, WeatherWrapper>>();
            messages = new HashMap<String, List<String>>();

            for (DayPeriod period : DayPeriod.values()) {
                HashMap<WeatherPriority, WeatherWrapper> periodMap = new HashMap<WeatherPriority, WeatherWrapper>();

                for (WeatherPriority priority : WeatherPriority.values()) {
                    periodMap.put(priority, new WeatherWrapper(WeatherType.UNDEFINED));
                }

                weatherMap.put(period, periodMap);
            }
        }

        public DaySummaryBuilder setWeatherWrapper(DayPeriod period, WeatherPriority priority, WeatherWrapper wrapper) {
            weatherMap.get(period).put(priority, wrapper);
            return this;
        }

        public DaySummaryBuilder setMessages(HashMap<String, List<String>> messages) {
            this.messages = messages;
            return this;
        }

        public DaySummary build() {
            return new DaySummary(this);
        }
    }

    public static class WeatherWrapper {
        private WeatherType weatherType;

        public WeatherWrapper(WeatherType weatherType) {
            this.weatherType = weatherType;
        }

        public WeatherType getType() {
            return weatherType;
        }

        @Override
        public String toString() {
            return weatherType.toString();
        }
    }
}
