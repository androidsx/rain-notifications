package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Table of expected forecasts. The first forecast in the table usually represents the current
 * weather.
 */
public class ForecastTableV2 {
    private final List<ForecastV2> mergedForecast;

    /**
     * @param forecastList An ordered list of forecasts without overlaps or gaps in their Intervals.
     */
    // TODO: Think about param conditions.
    public ForecastTableV2(List<ForecastV2> forecastList) {
        mergedForecast = new ArrayList<ForecastV2>();

        // Merge consecutive identical predictions
        if (forecastList.size() > 0) {
            mergedForecast.add(forecastList.get(0));
            for (ForecastV2 currentForecast : forecastList) {
                if (currentForecast.getWeatherWrapper().equals(mergedForecast.get(mergedForecast.size() -1).getWeatherWrapper())) {
                    ForecastV2 lastForecast = mergedForecast.remove(mergedForecast.size() -1);
                    mergedForecast.add(new ForecastV2(new Interval(lastForecast.getInterval().getStart(), currentForecast.getInterval().getEnd()), lastForecast.getWeatherWrapper()));
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

    /**
     * Returns the processed lists of forecasts. It is guaranteed to be non-empty.
     */
    public List<ForecastV2> getForecasts() {
        return mergedForecast;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ForecastV2 forecast : mergedForecast) {
            builder.append("\n" + forecast);
        }
        return builder.toString();
    }
}
