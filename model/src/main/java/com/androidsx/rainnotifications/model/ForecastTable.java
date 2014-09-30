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

    private List<Forecast> mergedForecastList;

    /**
     * @param forecastList An ordered list of forecasts without overlaps or gaps in their Intervals.
     */
    public static ForecastTable fromForecastList(List<Forecast> forecastList) {

        if (forecastList.isEmpty()) {
            throw new IllegalArgumentException("The list of forecasts is empty. At least one forecast is needed");
        } else {
            List<Forecast> meaningfulForecastList = getMeaningfulForecastList(forecastList);

            if(meaningfulForecastList.isEmpty()) {
                return null;
            }
            else {
                return new ForecastTable(getMergedForecastList(meaningfulForecastList));
            }
        }
    }

    private static List<Forecast> getMeaningfulForecastList(List<Forecast> forecastList) {
        List<WeatherType> meaningfulWeatherTypes = WeatherType.getMeaningfulWeatherTypes();
        List<Forecast> meaningfulForecastList = new ArrayList<Forecast>();

        for (Forecast forecast : forecastList) {
            if(meaningfulWeatherTypes.contains(forecast.getWeatherWrapper().getType())) {
                meaningfulForecastList.add(forecast);
            }
        }

        return meaningfulForecastList;
    }

    private static List<Forecast> getMergedForecastList(List<Forecast> forecastList) {
        List<Forecast> mergedForecastList = new ArrayList<Forecast>();
        mergedForecastList.add(forecastList.get(0));

        for (int i = 1 ; i < forecastList.size() ; i++) {
            Forecast currentForecast = forecastList.get(i);
            if (currentForecast.getWeatherWrapper().equals(mergedForecastList.get(mergedForecastList.size() -1).getWeatherWrapper())) {
                Forecast lastMergedForecast = mergedForecastList.remove(mergedForecastList.size() -1);
                mergedForecastList.add(new Forecast(new Interval(lastMergedForecast.getInterval().getStart(), currentForecast.getInterval().getEnd()), lastMergedForecast.getWeatherWrapper()));
            }
            else {
                mergedForecastList.add(currentForecast);
            }
        }

        return mergedForecastList;
    }

    private ForecastTable(List<Forecast> mergedForecastList) {
        this.mergedForecastList = mergedForecastList;
    }

    public DateTime getStart() {
        return mergedForecastList.get(0).getInterval().getStart();
    }

    public boolean hasTransitions() {
        return mergedForecastList.size() > 1; // Because the first one is the current weather
    }

    /**
     * Returns the processed lists of forecasts. It is guaranteed to be non-empty.
     */
    public List<Forecast> getForecastList() {
        return mergedForecastList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Forecast forecast : mergedForecastList) {
            builder.append("\n" + forecast);
        }
        return builder.toString();
    }
}
