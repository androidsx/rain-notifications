package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Table of expected forecasts. The first forecast in the table usually represents the current
 * weather.
 */
public class ForecastTable {
    private final List<Forecast> mergedForecast;

    /**
     * @param forecastList An ordered list of forecasts without overlaps or gaps in their Intervals.
     */
    public ForecastTable(List<Forecast> forecastList) {
        mergedForecast = new ArrayList<Forecast>();

        // Merge consecutive identical predictions
        if (forecastList.size() > 0) {
            mergedForecast.add(forecastList.get(0));
            for (Forecast currentForecast : forecastList) {
                if (currentForecast.getWeatherWrapper().equals(mergedForecast.get(mergedForecast.size() -1).getWeatherWrapper())) {
                    Forecast lastForecast = mergedForecast.remove(mergedForecast.size() -1);
                    mergedForecast.add(new Forecast(new Interval(lastForecast.getInterval().getStart(), currentForecast.getInterval().getEnd()), lastForecast.getWeatherWrapper()));
                }
                else {
                    mergedForecast.add(currentForecast);
                }
            }
        } else {
            throw new IllegalArgumentException("The list of forecasts is empty. At least one forecast is needed");
        }
    }

    public DateTime getStart() {
        return mergedForecast.get(0).getInterval().getStart();
    }

    public boolean hasTransitions() {
        return mergedForecast.size() > 1; // Because the first one is the current weather
    }

    /**
     * Returns the processed lists of forecasts. It is guaranteed to be non-empty.
     */
    public List<Forecast> getForecasts() {
        return mergedForecast;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Forecast forecast : mergedForecast) {
            builder.append("\n" + forecast);
        }
        return builder.toString();
    }
}
