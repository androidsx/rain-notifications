package com.androidsx.rainnotifications.model;

import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DaySummaryV2 {

    public static DaySummaryV2 fromForecastTable(ForecastTableV2 forecastTable) {
        DaySummaryBuilder builder = new DaySummaryBuilder();

        for (DayPeriod period : DayPeriod.values()) {
            HashMap<WeatherPriority, WeatherType> periodSummary = summarizeForecasts(filterForecasts(forecastTable.getForecasts(), period.getInterval(forecastTable.getStart())));
            for (WeatherPriority priority : periodSummary.keySet()) {
                builder.setWeatherType(period, priority, periodSummary.get(priority));
            }
        }

        return builder.build();
    }

    private static List<ForecastV2> filterForecasts(List<ForecastV2> forecasts, Interval interval) {
        List<ForecastV2> filteredForecasts = new ArrayList<ForecastV2>();

        for (ForecastV2 forecast : forecasts) {
            Interval overlap = forecast.getInterval().overlap(interval);
            if (overlap != null) {
                filteredForecasts.add(new ForecastV2(overlap, forecast.getWeatherWrapper()));
            }
        }

        return filteredForecasts;
    }

    private static HashMap<WeatherPriority, WeatherType> summarizeForecasts(List<ForecastV2> forecasts) {
        HashMap<WeatherPriority, WeatherType> summarizedForecasts = new HashMap<WeatherPriority, WeatherType>();

        if (forecasts.size() == 0) {
            summarizedForecasts.put(WeatherPriority.primary, WeatherType.UNDEFINED);
            summarizedForecasts.put(WeatherPriority.secondary, WeatherType.UNDEFINED);
        }
        else if (forecasts.size() == 1) {
            summarizedForecasts.put(WeatherPriority.primary, forecasts.get(0).getWeatherWrapper().getType());
            summarizedForecasts.put(WeatherPriority.secondary, WeatherType.UNDEFINED);
        }
        else {
            // We use most durable WeatherType as primary
            HashMap<WeatherType, Long> durations = new HashMap<WeatherType, Long>();
            WeatherType mostDurable = null;

            for (ForecastV2 forecast : forecasts) {
                long weatherTypeDuration = forecast.getInterval().toDurationMillis();

                if(durations.containsKey(forecast.getWeatherWrapper().getType())) {
                    weatherTypeDuration = durations.get(forecast.getWeatherWrapper().getType()) + weatherTypeDuration;
                }

                durations.put(forecast.getWeatherWrapper().getType(), weatherTypeDuration);

                if(mostDurable == null || durations.get(mostDurable) < weatherTypeDuration) {
                    mostDurable = forecast.getWeatherWrapper().getType();
                }
            }

            summarizedForecasts.put(WeatherPriority.primary, mostDurable);
            durations.remove(mostDurable);

            // and the highest priority as secondary
            if(durations.containsKey(WeatherType.RAIN)) {
                summarizedForecasts.put(WeatherPriority.secondary, WeatherType.RAIN);
            }
            else if(durations.containsKey(WeatherType.CLOUDY)) {
                summarizedForecasts.put(WeatherPriority.secondary, WeatherType.CLOUDY);
            }
            else if(durations.containsKey(WeatherType.PARTLY_CLOUDY)) {
                summarizedForecasts.put(WeatherPriority.secondary, WeatherType.PARTLY_CLOUDY);
            }
            else if(durations.containsKey(WeatherType.CLEAR)) {
                summarizedForecasts.put(WeatherPriority.secondary, WeatherType.CLEAR);
            }
            else {
                summarizedForecasts.put(WeatherPriority.secondary, WeatherType.UNDEFINED);
            }
        }
        return summarizedForecasts;
    }

    private final Random random = new Random();
    private HashMap<DayPeriod, HashMap<WeatherPriority, WeatherType>> weatherMap;
    private HashMap<String, List<String>> messages;

    private DaySummaryV2(DaySummaryBuilder builder) {
        this.weatherMap = builder.weatherMap;
        this.messages = builder.messages;
    }

    public void setWeatherType(DayPeriod period, WeatherPriority priority, WeatherType type) {
        weatherMap.get(period).put(priority, type);
    }

    public WeatherType getWeatherType(DayPeriod period, WeatherPriority priority) {
        return weatherMap.get(period).get(priority);
    }

    public void setMessages(HashMap<String, List<String>> messages) {
        this.messages = messages;
    }

    public String getDayMessage() {
        List<String> languageMessages = messages.get("en");
        return languageMessages != null ? languageMessages.get(random.nextInt(languageMessages.size())) : "Default"; // TODO: Review this message
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Day Messages for: ");

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

    public static class DaySummaryBuilder {
        private HashMap<DayPeriod, HashMap<WeatherPriority, WeatherType>> weatherMap;
        private HashMap<String, List<String>> messages;

        public DaySummaryBuilder() {
            weatherMap = new HashMap<DayPeriod, HashMap<WeatherPriority, WeatherType>>();
            messages = new HashMap<String, List<String>>();

            for (DayPeriod period : DayPeriod.values()) {
                HashMap<WeatherPriority, WeatherType> periodMap = new HashMap<WeatherPriority, WeatherType>();

                for (WeatherPriority priority : WeatherPriority.values()) {
                    periodMap.put(priority, WeatherType.UNDEFINED);
                }

                weatherMap.put(period, periodMap);
            }
        }

        public DaySummaryBuilder setWeatherType(DayPeriod period, WeatherPriority priority, WeatherType type) {
            weatherMap.get(period).put(priority, type);
            return this;
        }

        public DaySummaryBuilder setMessages(HashMap<String, List<String>> messages) {
            this.messages = messages;
            return this;
        }

        public DaySummaryV2 build() {
            return new DaySummaryV2(this);
        }
    }
}
