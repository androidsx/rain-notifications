package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DaySummary {

    public static DaySummary fromForecastTable(ForecastTable forecastTable) {
        forecastTable.getForecasts().add(createForecastFromBaseline(forecastTable));
        DaySummary.DaySummaryBuilder builder = new DaySummary.DaySummaryBuilder();

        for (DayPeriod period : DayPeriod.values()) {
            HashMap<WeatherPriority, DaySummary.WeatherWrapper> periodSummary = summarizeForecasts(filterForecasts(forecastTable.getForecasts(), period));
            for (WeatherPriority priority : periodSummary.keySet()) {
                builder.setWeatherWrapper(period, priority, periodSummary.get(priority));
            }
        }

        return builder.build();
    }

    private static List<Forecast> filterForecasts(List<Forecast> forecasts, DayPeriod period) {
        ArrayList<Forecast> filteredForecasts = new ArrayList<Forecast>();
        for (Forecast forecast : forecasts) {
            int startHourOfDayForecast = new DateTime(forecast.getTimeFromNow().getStartMillis()).getHourOfDay();
            int endHourOfDayForecast = new DateTime(forecast.getTimeFromNow().getEndMillis()).getHourOfDay();

            // The start or end hour within the limits, or overlapping across outside the limits
            if ((startHourOfDayForecast >= period.getStartHour() && startHourOfDayForecast <= period.getEndHour()) ||
                    (endHourOfDayForecast >= period.getStartHour() && endHourOfDayForecast <= period.getEndHour()) ||
                    (endHourOfDayForecast >= period.getEndHour() && startHourOfDayForecast <= period.getEndHour())) {
                filteredForecasts.add(forecast);
            }
        }

        return filteredForecasts;
    }

    private static HashMap<WeatherPriority, DaySummary.WeatherWrapper> summarizeForecasts(List<Forecast> forecasts) {
        HashMap<WeatherPriority, DaySummary.WeatherWrapper> priorityWeathers = new HashMap<WeatherPriority, DaySummary.WeatherWrapper>();

        if (forecasts.size() == 0) {
            return priorityWeathers;
        } else if (forecasts.size() == 1) {
            priorityWeathers.put(WeatherPriority.primary, new DaySummary.WeatherWrapper(forecasts.get(0).getForecastedWeather().getType()));
            return priorityWeathers;
        } else {
            // Sort by the bigger interval of time
            Collections.sort(forecasts, new Comparator<Forecast>() {
                @Override
                public int compare(Forecast o1, Forecast o2) {
                    if (o1.getTimeFromNow().isBefore(o2.getTimeFromNow())) {
                        return 1;
                    } else if (o1.getTimeFromNow().isEqual(o2.getTimeFromNow())) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            // TODO: if there are more than 2, check if the last forecast has more importance (such as rain)
            priorityWeathers.put(WeatherPriority.primary, new DaySummary.WeatherWrapper(forecasts.get(0).getForecastedWeather().getType()));
            priorityWeathers.put(WeatherPriority.secondary, new DaySummary.WeatherWrapper(forecasts.get(1).getForecastedWeather().getType()));
        }

        return priorityWeathers;
    }

    private static Forecast createForecastFromBaseline(ForecastTable forecastTable) {
        final Interval baselineInterval;
        if (forecastTable.getForecasts().size() > 0) {
            Interval firstForecastInterval = forecastTable.getForecasts().get(0).getTimeFromNow();
            baselineInterval = new Interval(forecastTable.getBaselineTime(), new DateTime(firstForecastInterval.getStartMillis()));
        } else {
            baselineInterval = new Interval(forecastTable.getBaselineTime(), forecastTable.getBaselineTime().plus(Period.days(1)));
        }
        return new Forecast(forecastTable.getBaselineWeather(), baselineInterval, Forecast.Granularity.HOUR);
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }

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
