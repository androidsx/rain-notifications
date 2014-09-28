package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

public class ForecastTableV2 {
    private final List<ForecastV2> mergedForecast;

    /**
     * @param forecastList An ordered list of forecasts without overlaps or gaps in their Intervals.
     */
    // TODO: Think about param conditions.
    public ForecastTableV2(List<ForecastV2> forecastList) {
        mergedForecast = new ArrayList<ForecastV2>();

        // Merge consecutive identical predictions
        if(forecastList.size() > 0) {
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
        }
    }

    public DateTime getStart() {
        return mergedForecast.size() != 0 ? mergedForecast.get(0).getInterval().getStart() : null;
    }

    public List<ForecastV2> getForecasts() {
        return mergedForecast;
    }

    @Override
    public String toString() {
        if (mergedForecast.isEmpty()) {
            return "<No forecasts>";
        } else {
            StringBuilder builder = new StringBuilder();
            for (ForecastV2 forecast : mergedForecast) {
                builder.append("\n" + forecast);
            }
            return builder.toString();
        }
    }
}
