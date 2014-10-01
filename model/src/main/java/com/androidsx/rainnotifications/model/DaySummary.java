package com.androidsx.rainnotifications.model;

import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class DaySummary {

    public static DaySummary fromForecastTable(ForecastTable forecastTable) {
        DaySummaryBuilder builder = new DaySummaryBuilder();

        for (DayPeriod period : DayPeriod.values()) {
            HashMap<WeatherPriority, WeatherType> periodSummary = summarizeForecasts(filterForecasts(forecastTable.getForecastList(), period.getInterval(forecastTable.getStart())));
            for (WeatherPriority priority : periodSummary.keySet()) {
                builder.setWeatherType(period, priority, periodSummary.get(priority));
            }
        }

        return builder.build();
    }

    private static List<Forecast> filterForecasts(List<Forecast> forecasts, Interval interval) {
        List<Forecast> filteredForecasts = new ArrayList<Forecast>();

        for (Forecast forecast : forecasts) {
            Interval overlap = forecast.getInterval().overlap(interval);
            if (overlap != null) {
                filteredForecasts.add(new Forecast(overlap, forecast.getWeatherWrapper()));
            }
        }

        return filteredForecasts;
    }

    private static HashMap<WeatherPriority, WeatherType> summarizeForecasts(List<Forecast> forecasts) {
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

            for (Forecast forecast : forecasts) {
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

    private DaySummary(DaySummaryBuilder builder) {
        this.weatherMap = builder.weatherMap;
        this.messages = builder.messages;
    }

    private void setWeatherType(DayPeriod period, WeatherPriority priority, WeatherType type) {
        weatherMap.get(period).put(priority, type);
    }

    public WeatherType getWeatherType(DayPeriod period, WeatherPriority priority) {
        return weatherMap.get(period).get(priority);
    }

    public String getDayMessage() {
        List<String> languageMessages = messages.get("en");
        return languageMessages != null ? languageMessages.get(random.nextInt(languageMessages.size())) : "Default"; // TODO: Review this message
    }

    public boolean downgrade() {
        // TODO: Discuss with team this method. I don't like it because it's not generic.
        if (!getWeatherType(DayPeriod.night, WeatherPriority.secondary).equals(WeatherType.UNDEFINED)) {
            Timber.d("remove night secondary");
            setWeatherType(DayPeriod.night, WeatherPriority.secondary, WeatherType.UNDEFINED);
            return true;
        }

        if (!getWeatherType(DayPeriod.night, WeatherPriority.primary).equals(WeatherType.UNDEFINED)) {
            Timber.d("remove night primary");
            setWeatherType(DayPeriod.night, WeatherPriority.primary, WeatherType.UNDEFINED);
            return true;
        }

        if (!getWeatherType(DayPeriod.evening, WeatherPriority.secondary).equals(WeatherType.UNDEFINED)) {
            Timber.d("remove evening secondary");
            setWeatherType(DayPeriod.evening, WeatherPriority.secondary, WeatherType.UNDEFINED);
            return true;
        }

        if (!getWeatherType(DayPeriod.evening, WeatherPriority.primary).equals(WeatherType.UNDEFINED)) {
            Timber.d("remove evening primary");
            setWeatherType(DayPeriod.evening, WeatherPriority.primary, WeatherType.UNDEFINED);
            return true;
        }

        if (!getWeatherType(DayPeriod.afternoon, WeatherPriority.secondary).equals(WeatherType.UNDEFINED)) {
            Timber.d("remove afternoon secondary");
            setWeatherType(DayPeriod.afternoon, WeatherPriority.secondary, WeatherType.UNDEFINED);
            return true;
        }

        if (!getWeatherType(DayPeriod.morning, WeatherPriority.secondary).equals(WeatherType.UNDEFINED)) {
            Timber.d("remove morning secondary");
            setWeatherType(DayPeriod.morning, WeatherPriority.secondary, WeatherType.UNDEFINED);
            return true;
        }

        if (!getWeatherType(DayPeriod.afternoon, WeatherPriority.primary).equals(WeatherType.UNDEFINED)) {
            Timber.d("remove afternoon primary");
            setWeatherType(DayPeriod.afternoon, WeatherPriority.primary, WeatherType.UNDEFINED);
            return true;
        }

        if (!getWeatherType(DayPeriod.morning, WeatherPriority.primary).equals(WeatherType.UNDEFINED)) {
            Timber.d("remove morning primary");
            setWeatherType(DayPeriod.morning, WeatherPriority.primary, WeatherType.UNDEFINED);
            return true;
        }

        return false;
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

        public DaySummary build() {
            return new DaySummary(this);
        }
    }
}
