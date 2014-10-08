package com.androidsx.rainnotifications.model;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DayTemplate {

    public enum DayTemplateJokerType {
        UNDEFINED,
        SAME;

        private boolean match(WeatherType currentType, WeatherType previousType) {
            if(previousType == null) { //This is for morning
                return true;
            }

            switch (this) {
                case SAME:
                    return currentType.equals(previousType);
                case UNDEFINED:
                    return !currentType.equals(previousType);
                default:
                    return false;
            }
        }
    }

    private final Random random = new Random();
    private HashMap<DayPeriod, HashMap<WeatherPriority, Object>> weatherMap;
    private HashMap<String, List<String>> messages;

    private DayTemplate(DayTemplateBuilder builder) {
        this.weatherMap = builder.weatherMap;
        this.messages = builder.messages;
    }

    public boolean match(Day day) {

        WeatherType lastPrimaryWeather = null;

        for (DayPeriod period : DayPeriod.values()) {

            // FIRST CHECK PRIMARY
            WeatherType currentPrimaryWeather = day.getWeatherType(period, WeatherPriority.primary);
            Object templatePrimaryWeather = getWeatherType(period, WeatherPriority.primary);

            if(currentPrimaryWeather == null && templatePrimaryWeather == null) {
                // true
            }
            else if(currentPrimaryWeather == null && templatePrimaryWeather != null) {
                return false;
            }
            else if(currentPrimaryWeather != null && templatePrimaryWeather == null) {
                return false;
            }
            else if(currentPrimaryWeather != null && templatePrimaryWeather != null) {

                if(templatePrimaryWeather instanceof DayTemplateJokerType) {
                    if(!((DayTemplateJokerType) templatePrimaryWeather).match(currentPrimaryWeather, lastPrimaryWeather)) {
                        return false;
                    }
                }
                else {
                    if(!currentPrimaryWeather.equals(templatePrimaryWeather)) {
                        return false;
                    }
                }

            }
            lastPrimaryWeather = currentPrimaryWeather;

            // NOW CHECK SECONDARY
            WeatherType currentSecondaryWeather = day.getWeatherType(period, WeatherPriority.secondary);
            WeatherType templateSecondaryWeather = (WeatherType) getWeatherType(period, WeatherPriority.secondary);

            if(currentSecondaryWeather == null && templateSecondaryWeather == null) {
                // true
            }
            else if(currentSecondaryWeather == null && templateSecondaryWeather != null) {
                return false;
            }
            else if(currentSecondaryWeather != null && templateSecondaryWeather == null) {
                return false;
            }
            else if(currentSecondaryWeather != null && templateSecondaryWeather != null) {
                if(!currentSecondaryWeather.equals(templateSecondaryWeather)) {
                    return false;
                }
            }
        }

        return true;
    }

    //TODO: ReImplement with Multilanguage support.
    public String resolveMessage(Context context, Day day) {
        String message = messages.get("en").get(random.nextInt(messages.get("en").size()));
        for (DayPeriod period : DayPeriod.values()) {
            message = message.replace("${weather_" + period + "_adj}", day.getWeatherType(period, WeatherPriority.primary).getAdjective(context));
            message = message.replace("${weather_" + period + "_ing}", day.getWeatherType(period, WeatherPriority.primary).getGerund(context));
        }
        return message;
    }

    private Object getWeatherType(DayPeriod period, WeatherPriority priority) {
        return weatherMap.get(period).get(priority);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DayTemplate for: ");

        for (DayPeriod period : DayPeriod.values()) {
            for (WeatherPriority priority : WeatherPriority.values()) {
                builder.append("\n" + period + " " + priority + " weather: " + getWeatherType(period, priority));
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

    public static class DayTemplateBuilder {
        private HashMap<DayPeriod, HashMap<WeatherPriority, Object>> weatherMap;
        private HashMap<String, List<String>> messages;

        public DayTemplateBuilder() {
            weatherMap = new HashMap<DayPeriod, HashMap<WeatherPriority, Object>>();
            messages = new HashMap<String, List<String>>();

            for (DayPeriod period : DayPeriod.values()) {
                HashMap<WeatherPriority, Object> periodMap = new HashMap<WeatherPriority, Object>();

                for (WeatherPriority priority : WeatherPriority.values()) {
                    periodMap.put(priority, null);
                }

                weatherMap.put(period, periodMap);
            }
        }

        public DayTemplateBuilder setWeatherType(DayPeriod period, WeatherPriority priority, WeatherType type) {
            weatherMap.get(period).put(priority, type);
            return this;
        }

        public DayTemplateBuilder setWeatherType(DayPeriod period, WeatherPriority priority, DayTemplateJokerType type) {
            if(priority.equals(WeatherPriority.primary)) {
                weatherMap.get(period).put(priority, type);
                return this;
            }
            throw new IllegalArgumentException("Joker types are only allowed for primary priority");
        }

        public DayTemplateBuilder setMessages(HashMap<String, List<String>> messages) {
            this.messages = messages;
            return this;
        }

        public DayTemplate build() {
            return new DayTemplate(this);
        }
    }
}
