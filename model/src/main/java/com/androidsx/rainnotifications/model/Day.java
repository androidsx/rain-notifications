package com.androidsx.rainnotifications.model;

import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Day {

    private HashMap<DayPeriod, HashMap<WeatherPriority, WeatherType>> weatherMap;
    private Forecast minTemperature;
    private Forecast maxTemperature;

    public Day(ForecastTable forecastTable) {
        weatherMap = new HashMap<DayPeriod, HashMap<WeatherPriority, WeatherType>>();
        for (DayPeriod period : DayPeriod.values()) {
            weatherMap.put(period, summarizeForecasts(filterForecasts(forecastTable.getForecastList(), period.getInterval(forecastTable.getStart()))));
        }
        setMinMaxTemperature(forecastTable.getForecastList(),
                new Interval(DayPeriod.MORNING.getInterval(forecastTable.getStart()).getStart(), DayPeriod.EVENING.getInterval(forecastTable.getStart()).getEnd()));
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
            summarizedForecasts.put(WeatherPriority.primary, forecasts.get(0).getWeatherWrapper().getWeatherType());
            summarizedForecasts.put(WeatherPriority.secondary, null);
        }
        else {
            // We use most durable WeatherType as primary
            HashMap<WeatherType, Long> durations = new HashMap<WeatherType, Long>();
            WeatherType mostDurable = null;

            for (Forecast forecast : forecasts) {
                long weatherTypeDuration = forecast.getInterval().toDurationMillis();

                if(durations.containsKey(forecast.getWeatherWrapper().getWeatherType())) {
                    weatherTypeDuration = durations.get(forecast.getWeatherWrapper().getWeatherType()) + weatherTypeDuration;
                }

                durations.put(forecast.getWeatherWrapper().getWeatherType(), weatherTypeDuration);

                if(mostDurable == null || durations.get(mostDurable) < weatherTypeDuration) {
                    mostDurable = forecast.getWeatherWrapper().getWeatherType();
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

    private void setMinMaxTemperature(List<Forecast> forecasts, Interval interval) {
        for (Forecast forecast : forecasts) {
            if (forecast.getInterval().overlap(interval) != null) {
                if(minTemperature == null || minTemperature.getWeatherWrapper().getTemperatureCelsius() > forecast.getWeatherWrapper().getTemperatureCelsius()) {
                    minTemperature = forecast;
                }

                if(maxTemperature == null || maxTemperature.getWeatherWrapper().getTemperatureCelsius() < forecast.getWeatherWrapper().getTemperatureCelsius()) {
                    maxTemperature = forecast;
                }
            }
        }
    }

    public WeatherType getWeatherType(DayPeriod period, WeatherPriority priority) {
        return weatherMap.get(period).get(priority);
    }

    public Forecast getMinTemperature() {
        return minTemperature;
    }

    public Forecast getMaxTemperature() {
        return maxTemperature;
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

        builder.append("\n     Min temperature on " + minTemperature);
        builder.append("\n     Max temperature on " + maxTemperature);

        return builder.toString();
    }
}
