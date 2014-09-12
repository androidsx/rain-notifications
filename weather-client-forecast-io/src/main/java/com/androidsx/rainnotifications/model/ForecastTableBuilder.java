package com.androidsx.rainnotifications.model;

import android.util.Log;

import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.DataBlock;
import com.forecast.io.v2.transfer.DataPoint;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link ForecastTable}.
 * <p>
 * Should not be used from outside of this project.
 */
public class ForecastTableBuilder {

    public static ForecastTable buildFromForecastIo(ForecastService.Response response) {
        final DataPoint currently = response.getForecast().getCurrently();
        final DataBlock minutely = response.getForecast().getMinutely();
        final DataBlock hourly = response.getForecast().getHourly();
        final DateTime currentTime = new DateTime(currently.getTime() * 1000);

        final DateTime sunriseTime = new DateTime(response.getForecast().getDaily().getData().get(0).getSunriseTime());
        final DateTime sunsetTime = new DateTime(response.getForecast().getDaily().getData().get(0).getSunsetTime());
        Log.d("Forecast.io", "Sunrise: " + sunriseTime + " - Sunset: " + sunsetTime);
        final List<Forecast> allForecasts = new ArrayList<Forecast>();
        allForecasts.addAll(extractAllValidForecast(currentTime, minutely, Forecast.Granularity.MINUTE));
        allForecasts.addAll(extractAllValidForecast(currentTime, hourly, Forecast.Granularity.HOUR));

        final Weather currentWeather = WeatherBuilder.buildFromForecastIo(currently);

        return ForecastTable.create(currentWeather, currentTime, allForecasts);
    }

    private static List<Forecast> extractAllValidForecast(DateTime fromTime,
                                                          DataBlock dataBlock,
                                                          Forecast.Granularity granularity) {
        final List<Forecast> forecasts = new ArrayList<Forecast>();
        if (dataBlock != null) {
            for (DataPoint dataPoint : dataBlock.getData()) {
                final Forecast forecast = extractForecastIfValid(fromTime, dataPoint, granularity);
                if (forecast != null) {
                    forecasts.add(forecast);
                }
            }
        }
        return forecasts;
    }

    private static Forecast extractForecastIfValid(DateTime fromTime,
                                                   DataPoint dataPoint,
                                                   Forecast.Granularity granularity) {
        final Weather forecastedWeather = WeatherBuilder.buildFromForecastIo(dataPoint);
        final DateTime forecastTime = new DateTime(dataPoint.getTime() * 1000);

        if (forecastTime.isBefore(fromTime.toInstant())) {
            //Log.v(TAG, "Skip the forecast for the present interval at " + forecastTime);
            return null;
        } else {
            final Interval timeFromNow = new Interval(fromTime, forecastTime);
            return new Forecast(forecastedWeather, timeFromNow, granularity);
        }
    }

    /*private static void setProperPhase(DateTime weatherTime, JSONObject sunPhase, Weather weather) {
        DateTime sunriseTime = getSunPhaseTime(sunPhase, "sunrise");
        DateTime sunsetTime = getSunPhaseTime(sunPhase, "sunset");
        if (sunriseTime.isBefore(weatherTime) && weatherTime.isBefore(sunsetTime)) {
            // Do nothing
        } else {
            weather.setPhase(WeatherPhase.NIGHT);
        }
    }

    private static DateTime getSunPhaseTime(JSONObject sunPhase, String phase) {
        final JSONObject sunrise = (JSONObject) sunPhase.get(phase);
        DateTime sunPhaseTime = DateTime.now();
        sunPhaseTime = sunPhaseTime.hourOfDay().setCopy(sunrise.getString("hour"));
        sunPhaseTime = sunPhaseTime.minuteOfHour().setCopy(sunrise.getString("minute"));
        return sunPhaseTime;
    }*/
}
