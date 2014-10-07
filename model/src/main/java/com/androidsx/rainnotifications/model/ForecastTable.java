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

    private List<Forecast> forecastList;
    private static List<Forecast> rawForecastList;

    /**
     * Returns an appropiate {@link com.androidsx.rainnotifications.model.ForecastTable} for the given forecastList. It processed the given list as follows:
     *
     * <ol>
     * <li>Remove not meaningful Forecasts from the list</li>
     * <li>Merge consecutive Forecasts with same {@link com.androidsx.rainnotifications.model.WeatherType}</li>
     * </ol>
     *
     * As a result of {@link #getMeaningfulForecastList(java.util.List)} and {@link #getMergedForecastList(java.util.List)}
     * the gaps after remove not meaningful Forecast are filled by merging previous and subsequent Forecast if they has the
     * same WeatherType. Otherwise the table contains gaps that are not taken into account for {@link com.androidsx.rainnotifications.model.DaySummary#fromForecastTable(ForecastTable)}
     *
     * @param forecastList An ordered list of {@link com.androidsx.rainnotifications.model.Forecast} without overlaps or gaps in their Intervals.
     * @return {@link com.androidsx.rainnotifications.model.ForecastTable} if processed forecastList isn't empty, null in other case.
     * @throws java.lang.IllegalArgumentException if the given forecastList is empty
     */
    public static ForecastTable fromForecastList(List<Forecast> forecastList) {
        rawForecastList = forecastList;
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

    private ForecastTable(List<Forecast> forecastList) {
        this.forecastList = forecastList;
    }

    public DateTime getStart() {
        return forecastList.get(0).getInterval().getStart();
    }

    public boolean hasTransitions() {
        return forecastList.size() > 1; // Because the first one is the current weather
    }

    /**
     * Returns the processed lists of forecasts. It is guaranteed to be non-empty.
     */
    public List<Forecast> getForecastList() {
        return forecastList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Forecast forecast : forecastList) {
            builder.append("\n" + forecast);
        }
        return builder.toString();
    }

    public List<Forecast> getRawForecastList() {
        return rawForecastList;
    }
}
