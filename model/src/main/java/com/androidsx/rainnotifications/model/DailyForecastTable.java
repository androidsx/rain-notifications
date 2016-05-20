package com.androidsx.rainnotifications.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Table of expected forecasts. The first forecast in the table usually represents the current
 * weather.
 */
public class DailyForecastTable {

    private List<DailyForecast> dailyForecastList;

    /**
     * Returns an appropriate {@link com.androidsx.rainnotifications.model.DailyForecastTable} for the given list of Forecast.
     * It removes not meaningful Forecasts from given list.
     *
     * @param dailyForecasts An ordered list of {@link Forecast} without overlaps or gaps in their Intervals.
     *        Intervals must be of one-hour
     * @return {@link com.androidsx.rainnotifications.model.DailyForecastTable} if processed dailyForecastList isn't empty, null in other case.
     * @throws IllegalArgumentException if the given dailyForecastList is empty
     */
    public static DailyForecastTable fromForecastList(List<DailyForecast> dailyForecasts) {
        // TODO: No teng nada claro esto de eliminar los UNKNOWN... hablarlo con Omar
        // TODO: Think about if we need to check the day conditions
        if (dailyForecasts.isEmpty()) {
            throw new IllegalArgumentException("The list of forecasts is empty. At least one forecast is needed");
        } else {
            List<DailyForecast> meaningfulForecastList = getMeaningfulForecastList(dailyForecasts);
            return meaningfulForecastList.isEmpty() ? null : new DailyForecastTable(meaningfulForecastList);
        }
    }

    private static List<DailyForecast> getMeaningfulForecastList(List<DailyForecast> forecastList) {
        List<WeatherType> meaningfulWeatherTypes = WeatherType.getMeaningfulWeatherTypes();
        List<DailyForecast> meaningfulForecastList = new ArrayList<DailyForecast>();

        for (DailyForecast forecast : forecastList) {
            if(meaningfulWeatherTypes.contains(forecast.getWeatherWrapper().getWeatherType())) {
                meaningfulForecastList.add(forecast);
            }
        }

        return meaningfulForecastList;
    }

    private DailyForecastTable(List<DailyForecast> dailyForecastList) {
        this.dailyForecastList = dailyForecastList;
    }

    /**
     * Returns the processed lists of daily forecasts. It is guaranteed to be non-empty.
     */
    public List<DailyForecast> getDailyForecastList() {
        return dailyForecastList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DAY FORECAST_TABLE:");
        for (DailyForecast forecast : dailyForecastList) {
            builder.append("\n     " + forecast);
        }
        return builder.toString();
    }
}
