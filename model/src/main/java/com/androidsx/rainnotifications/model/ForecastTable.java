package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Table of expected forecasts. The first forecast in the table usually represents the current
 * weather.
 */
public class ForecastTable {

    private List<Forecast> hourlyForecastList;
    private Forecast firstTransitionForecast;

    //TODO: Update this javadoc
    /**
     * Returns an appropiate {@link com.androidsx.rainnotifications.model.ForecastTable} for the given hourlyForecastList. It processed the given list as follows:
     *
     * <ol>
     * <li>Remove not meaningful Forecasts from the list</li>
     * <li>Merge consecutive Forecasts with same {@link com.androidsx.rainnotifications.model.WeatherType}</li>
     * </ol>
     *
     * As a result of {@link #getMeaningfulForecastList(java.util.List)} and {@link #getMergedForecastList(java.util.List)}
     * the gaps after remove not meaningful Forecast are filled by merging previous and subsequent Forecast if they has the
     * same WeatherType. Otherwise the table contains gaps that are not taken into account for {@link com.androidsx.rainnotifications.model.Day#Day(ForecastTable)}
     *
     * @param forecastList An ordered list of {@link com.androidsx.rainnotifications.model.Forecast} without overlaps or gaps in their Intervals.
     * @return {@link com.androidsx.rainnotifications.model.ForecastTable} if processed hourlyForecastList isn't empty, null in other case.
     * @throws java.lang.IllegalArgumentException if the given hourlyForecastList is empty
     */
    public static ForecastTable fromForecastList(List<Forecast> forecastList) {

        //TODO: Check hourly format.

        if (forecastList.isEmpty()) {
            throw new IllegalArgumentException("The list of forecasts is empty. At least one forecast is needed");
        } else {
            List<Forecast> meaningfulForecastList = getMeaningfulForecastList(forecastList);
            return meaningfulForecastList.isEmpty() ? null : new ForecastTable(meaningfulForecastList);
        }
    }

    private static List<Forecast> getMeaningfulForecastList(List<Forecast> forecastList) {
        List<WeatherType> meaningfulWeatherTypes = WeatherType.getMeaningfulWeatherTypes();
        List<Forecast> meaningfulForecastList = new ArrayList<Forecast>();

        for (Forecast forecast : forecastList) {
            if(meaningfulWeatherTypes.contains(forecast.getWeatherWrapper().getWeatherType())) {
                meaningfulForecastList.add(forecast);
            }
        }

        return meaningfulForecastList;
    }

    private ForecastTable(List<Forecast> hourlyForecastList) {
        this.hourlyForecastList = hourlyForecastList;

        for (Forecast current : getHourlyForecastList()) {
            if(!current.getWeatherWrapper().getWeatherType().equals(getBaselineForecast().getWeatherWrapper().getWeatherType())) {
                firstTransitionForecast = current;
                return;
            }
        }
    }

    /**
     * Returns the processed lists of hourly forecasts. It is guaranteed to be non-empty.
     */
    public List<Forecast> getHourlyForecastList() {
        return hourlyForecastList;
    }

    public DateTime getBaselineStart() {
        return getBaselineForecast().getInterval().getStart();
    }

    public Forecast getBaselineForecast() {
        return hourlyForecastList.get(0);
    }

    public boolean hasTransitions() {
        return firstTransitionForecast != null;
    }

    public Forecast getFirstTransitionForecast() {
        return firstTransitionForecast;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FORECAST_TABLE: ");
        for (Forecast forecast : hourlyForecastList) {
            builder.append("\n" + forecast);
        }
        return builder.toString();
    }
}
