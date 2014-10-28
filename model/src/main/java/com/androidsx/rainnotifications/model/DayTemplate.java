package com.androidsx.rainnotifications.model;

import android.content.Context;

import com.androidsx.rainnotifications.model.util.UiUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DayTemplate {

    /**
     * README before create templates on json files.
     * <p>
     * WHATEVER: It is ONLY valid for SECONDARIES. It means there must be secondary REGARDLESS of the particular WeatherType
     * <p>
     * OTHER: It is ONLY valid for PRIMARIES. It means this WeatherType must be DIFFERENT than previous primary WeatherType.
     *        If it's used as a first primary on a Day, it means there must be primary REGARDLESS of the particular WeatherType
     * <p>
     * SAME: It is ONLY valid for PRIMARIES. It means this WeatherType must be EQUAL than previous primary WeatherType.
     * <p>
     * Some clarifications: <p>
     * - Secondaries WeatherTypes are optional on a Day. <p>
     * - A Day can have null primary values, but if one primary is non-null, all following primaries must be non-null. <p>
     * - The first primary on a Day can't be SAME, so can be only a particular WeatherType or OTHER. <p>
     */
    public enum DayTemplateJokerType {
        WHATEVER, // Only for secondary
        OTHER, // Only for primary
        SAME; // Only for primary

        private boolean match(WeatherType currentType, WeatherType previousType) {
            switch (this) {
                case WHATEVER:
                    return true;
                case SAME:
                    return previousType == null ? false : currentType.equals(previousType);
                case OTHER:
                    // previousType == null means currentType is the first period on day.
                    return previousType == null ? true : !currentType.equals(previousType);
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
        // TODO: Simplify this code.
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
            Object templateSecondaryWeather = getWeatherType(period, WeatherPriority.secondary);

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

                if(templateSecondaryWeather instanceof DayTemplateJokerType) {
                    if(!((DayTemplateJokerType) templateSecondaryWeather).match(currentSecondaryWeather, null)) {
                        return false;
                    }
                }
                else {
                    if(!currentSecondaryWeather.equals(templateSecondaryWeather)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    //TODO: ReImplement with Multilanguage support.
    public String resolveMessage(Context context, Day day) {
        String message = messages.get("en").get(random.nextInt(messages.get("en").size()));
        for (DayPeriod period : DayPeriod.values()) {
            for(WeatherPriority priority : WeatherPriority.values()) {
                WeatherType type = day.getWeatherType(period, priority);
                if(type != null) {
                    message = message.replace("${weather_" + period + "_" + priority + "_noun}", type.getNoun(context));
                    message = message.replace("${weather_" + period + "_" + priority + "_adj}", type.getAdjective(context));
                    message = message.replace("${weather_" + period + "_" + priority + "_ing}", type.getGerund(context));
                }
            }
        }

        message = message + context.getString(R.string.temperature_message,
                day.getMinTemperature().getWeatherWrapper().getReadableTemperature(context),
                UiUtil.getReadableHour(day.getMinTemperature().getInterval().getStart()),
                day.getMaxTemperature().getWeatherWrapper().getReadableTemperature(context),
                UiUtil.getReadableHour(day.getMaxTemperature().getInterval().getStart()));
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
            if(priority.equals(WeatherPriority.primary) && type.equals(DayTemplateJokerType.WHATEVER)) {
                throw new IllegalArgumentException("WHATEVER Joker type aren't allowed for primary priority");
            }
            else if(priority.equals(WeatherPriority.secondary) && !type.equals(DayTemplateJokerType.WHATEVER)) {
                throw new IllegalArgumentException("Only WHATEVER Joker type are allowed for secondary priority");
            }
            else {
                weatherMap.get(period).put(priority, type);
                return this;
            }
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
