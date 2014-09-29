package com.androidsx.rainnotifications.model;

import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.DataPoint;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link com.androidsx.rainnotifications.model.ForecastTableV2}.
 * <p>
 * Should not be used from outside of this project.
 */
public class ForecastTableBuilder {
    public static ForecastTableV2 buildFromForecastIo(ForecastService.Response response) {
        List<DataPoint> dataPoints = new ArrayList<DataPoint>();
        dataPoints.add(response.getForecast().getCurrently());
        dataPoints.addAll(response.getForecast().getHourly().getData());

        return new ForecastTableV2(getForecastList(dataPoints));
    }

    private static List<ForecastV2> getForecastList(List<DataPoint> dataPoints) {
        List<ForecastV2> forecasts = new ArrayList<ForecastV2>();

        for (int i = 0 ; i < dataPoints.size() - 1 ; i++) {
            forecasts.add(new ForecastV2(getDataPointInterval(dataPoints.get(i), dataPoints.get(i + 1)), WeatherBuilder.buildFromForecastIo(dataPoints.get(i))));
        }
        forecasts.add(new ForecastV2(getDataPointInterval(dataPoints.get(dataPoints.size() - 1), null), WeatherBuilder.buildFromForecastIo(dataPoints.get(dataPoints.size() - 1))));

        return forecasts;
    }

    private static Interval getDataPointInterval(DataPoint dataPoint, DataPoint nextDataPoint) {
        if(nextDataPoint == null) {
            DateTime dataPointStart = getStartDateTime(dataPoint);
            return new Interval(dataPointStart, DayPeriod.night.getInterval(dataPointStart).getEnd());
        }
        else {
            return new Interval(getStartDateTime(dataPoint), getStartDateTime(nextDataPoint));
        }
    }

    private static DateTime getStartDateTime(DataPoint dataPoint) {
        return new DateTime(dataPoint.getTime() * 1000);
    }
}
