package com.androidsx.rainnotifications.model;

import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Day {

    private HashMap<DayPeriod, HashMap<WeatherPriority, WeatherType>> weatherMap;

    public Day(ForecastTable forecastTable) {
        weatherMap = new HashMap<DayPeriod, HashMap<WeatherPriority, WeatherType>>();
        for (DayPeriod period : DayPeriod.values()) {
            weatherMap.put(period, summarizeForecasts(filterForecasts(forecastTable.getForecastList(), period.getInterval(forecastTable.getStart()))));
        }
    }

    private List<Forecast> filterForecasts(List<Forecast> forecasts, Interval interval) {
        List<Forecast> filteredForecasts = new ArrayList<Forecast>();

        for (Forecast forecast : forecasts) {
            Interval overlap = forecast.getInterval().overlap(interval);
            if (overlap != null) {
                filteredForecasts.add(new Forecast(overlap, forecast.getWeatherWrapper()));
            }
        }

        return filteredForecasts;
    }

    private HashMap<WeatherPriority, WeatherType> summarizeForecasts(List<Forecast> forecasts) {
        HashMap<WeatherPriority, WeatherType> summarizedForecasts = new HashMap<WeatherPriority, WeatherType>();

        if (forecasts.size() == 0) {
            summarizedForecasts.put(WeatherPriority.primary, null);
            summarizedForecasts.put(WeatherPriority.secondary, null);
        }
        else if (forecasts.size() == 1) {
            summarizedForecasts.put(WeatherPriority.primary, forecasts.get(0).getWeatherWrapper().getType());
            summarizedForecasts.put(WeatherPriority.secondary, null);
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

            // and the most relevant as secondary (only if it is more relevant than primary)
            durations.remove(mostDurable);
            if(durations.isEmpty()) {
                summarizedForecasts.put(WeatherPriority.secondary, null);
            }
            else {
                List<WeatherType> secondaryWeathers = new ArrayList<WeatherType>(durations.keySet());
                Collections.sort(secondaryWeathers, new Comparator<WeatherType>() {
                    @Override
                    public int compare(WeatherType lhs, WeatherType rhs) {
                        return rhs.getRelevance() - lhs.getRelevance();
                    }
                });

                if(secondaryWeathers.get(0).getRelevance() > mostDurable.getRelevance()) {
                    summarizedForecasts.put(WeatherPriority.secondary, secondaryWeathers.get(0));
                }
                else {
                    summarizedForecasts.put(WeatherPriority.secondary, null);
                }
            }
        }

        return summarizedForecasts;
    }

    public WeatherType getWeatherType(DayPeriod period, WeatherPriority priority) {
        return weatherMap.get(period).get(priority);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DAY: ");

        for (DayPeriod period : DayPeriod.values()) {
            for (WeatherPriority priority : WeatherPriority.values()) {
                builder.append("\n     " + period + " " + priority + " weather: " + getWeatherType(period, priority));
            }
        }

        return builder.toString();
    }
}
