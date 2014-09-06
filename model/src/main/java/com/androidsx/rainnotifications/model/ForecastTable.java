package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ForecastTable {
    private final Weather baselineWeather;
    private final DateTime baselineTime;
    private final List<Forecast> forecasts;

    private ForecastTable(Weather baselineWeather, DateTime baselineTime, List<Forecast> forecasts) {
        this.baselineWeather = baselineWeather;
        this.baselineTime = baselineTime;
        this.forecasts = forecasts;
    }

    /**
     * Weather that was used as the baseline for the computation of the forecast transitions.
     * Usually, the weather at the current time.
     *
     * @return baseline weather
     */
    public Weather getBaselineWeather() {
        return baselineWeather;
    }

    /**
     * Returns a filtered list of forecasts to show only the weather transitions. For instance, 5
     * hours of sun followed by 2 hours of rain would just return 2 forecasts.
     *
     * @return forecast transitions
     */
    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public static ForecastTable create(Weather currentWeather, DateTime baselineTime, List<Forecast> allForecasts) {
        final List<Forecast> transitions = new ArrayList<Forecast>();

        Weather latestWeather = currentWeather;
        for (Forecast forecast : allForecasts) {
            final Weather forecastedWeather = forecast.getForecastedWeather();
            if (latestWeather.equals(forecastedWeather)) {
                // Skip it
            } else {
                transitions.add(forecast);
                if(!forecastedWeather.isUnknownWeather()) {
                    latestWeather = forecastedWeather;
                }
            }
        }
        return new ForecastTable(currentWeather, baselineTime, transitions);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Baseline weather ")
                .append(baselineWeather)
                .append(" at ")
                .append(baselineTime)
                .append(". Forecasts:");
        if (forecasts.isEmpty()) {
            builder.append(" <No forecasts>");
        } else {
            builder.append('\n');
            for (Forecast forecast : forecasts) {
                builder.append("* ").append(forecast).append("\n");
            }
        }

        return builder.toString();
    }
}
