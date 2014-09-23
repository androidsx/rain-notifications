package com.androidsx.rainnotifications.alert;

import android.content.Context;
import android.util.SparseArray;

import com.androidsx.rainnotifications.model.DaySummary;
import com.androidsx.rainnotifications.model.DaySummaryDeserializer;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.WeatherType;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DaySummaryGenerator {

    private static final int MORNING_START_HOUR = 7;
    private static final int MORNING_END_HOUR = 12;

    private static final int AFTERNOON_START_HOUR = 12;
    private static final int AFTERNOON_END_HOUR = 20;

    private final Context context;
    private HashMap<String, DaySummary> sumariesMap;

    public DaySummaryGenerator(Context context) {
        this.context = context;
    }

    public void init() {
        final InputStream dayMessagesJsonInputStream;
        try {
            dayMessagesJsonInputStream = context.getResources().getAssets().open("dayMessages.json");
        } catch (IOException e) {
            throw new IllegalStateException("Can't parse the day messages JSON file", e);
        }

        init(dayMessagesJsonInputStream);
    }

    public void init(InputStream dayMessagesJsonInputStream) {
        final Reader jsonReader = new InputStreamReader(dayMessagesJsonInputStream);
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DaySummary.class, new DaySummaryDeserializer());

        sumariesMap = new HashMap<String, DaySummary>();
        SparseArray<ArrayList<DaySummary>> dispersedSumaries = disparseSummaries(Arrays.asList(gsonBuilder.create().fromJson(jsonReader, DaySummary[].class)));

        for(int i = 0 ; i < DaySummary.MAX_WEATHER_LEVEL ; i++) {
            for(DaySummary daySummary : dispersedSumaries.get(i)) {
                for(String key : daySummary.getSuitableWeathersKeys()) {
                    if(!sumariesMap.containsKey(key)) {
                        sumariesMap.put(key,daySummary);
                    }
                }
            }
        }
    }

    private SparseArray<ArrayList<DaySummary>> disparseSummaries(List<DaySummary> daySummaries) {
        SparseArray<ArrayList<DaySummary>> dispersed = new SparseArray<ArrayList<DaySummary>>();

        for(int i = 0 ; i < DaySummary.MAX_WEATHER_LEVEL ; i++) {
            dispersed.append(i, new ArrayList<DaySummary>());
        }

        for(DaySummary daySummary : daySummaries) {
            dispersed.get(daySummary.getWhateverLevel()).add(daySummary);
        }

        return dispersed;
    }

    public DaySummary getDaySummary(ForecastTable forecastTable) {
        // TODO: This is only a temporary implementation that can and should be improved
        DateTime now = DateTime.now().minuteOfDay().setCopy(0).secondOfDay().setCopy(0).millisOfDay().setCopy(0);
        DateTime morningStart = now.hourOfDay().setCopy(MORNING_START_HOUR);
        DateTime morningEnd = now.hourOfDay().setCopy(MORNING_END_HOUR);
        DateTime afternoonStart = now.hourOfDay().setCopy(AFTERNOON_START_HOUR);
        DateTime afternoonEnd = now.hourOfDay().setCopy(AFTERNOON_END_HOUR);

        ArrayList<WeatherType> morningWeathers = new ArrayList<WeatherType>();
        ArrayList<WeatherType> afternoonWeathers = new ArrayList<WeatherType>();

        morningWeathers.add(forecastTable.getBaselineWeather().getType());

        for(Forecast forecast : forecastTable.getForecasts()) {
            DateTime forecastStart = new DateTime(forecast.getTimeFromNow().getEndMillis());

            if(morningStart.isBefore(forecastStart) && forecastStart.isBefore(morningEnd)) {
                morningWeathers.add(forecast.getForecastedWeather().getType());
            }
        }

        afternoonWeathers.add(morningWeathers.get(morningWeathers.size()-1));

        for(Forecast forecast : forecastTable.getForecasts()) {
            DateTime forecastStart = new DateTime(forecast.getTimeFromNow().getEndMillis());

            if(afternoonStart.isBefore(forecastStart) && forecastStart.isBefore(afternoonEnd)) {
                afternoonWeathers.add(forecast.getForecastedWeather().getType());
            }
        }

        return getDaySummary(getMoreSignificantWeather(morningWeathers), getMoreSignificantWeather(afternoonWeathers));
    }

    private WeatherType getMoreSignificantWeather(ArrayList<WeatherType> weathers) {
        if(weathers.contains(WeatherType.RAIN)) {
            return WeatherType.RAIN;
        }

        if(weathers.contains(WeatherType.CLOUDY)) {
            return WeatherType.CLOUDY;
        }

        if(weathers.contains(WeatherType.PARTLY_CLOUDY)) {
            return WeatherType.PARTLY_CLOUDY;
        }

        if(weathers.contains(WeatherType.CLEAR)) {
            return WeatherType.CLEAR;
        }

        if(weathers.contains(WeatherType.CLOUDY_NIGHT)) {
            return WeatherType.CLOUDY_NIGHT;
        }

        if(weathers.contains(WeatherType.PARTLY_CLOUDY_NIGHT)) {
            return WeatherType.PARTLY_CLOUDY_NIGHT;
        }

        if(weathers.contains(WeatherType.CLEAR_NIGHT)) {
            return WeatherType.CLEAR_NIGHT;
        }

        return WeatherType.CLEAR;
    }

    private DaySummary getDaySummary(WeatherType morning, WeatherType afternoon) {
        String tmpKey = morning.toString() + "_UNDEFINED_" + afternoon.toString() + "_UNDEFINED_UNDEFINED_UNDEFINED_UNDEFINED_UNDEFINED";

        if(sumariesMap.containsKey(tmpKey)) {
            return sumariesMap.get(tmpKey);
        }

        //TODO: Remove Workaround
        DaySummary daySummary = new DaySummary();
        HashMap<String, List<String>> messages = new HashMap<String, List<String>>();
        messages.put("en", Arrays.asList("No key for: " + tmpKey));
        daySummary.setMessages(messages);

        return daySummary;

        //throw new IllegalStateException("Unable to find a suitable DaySummary for morning " + morning + " and afternoon " + afternoon);
    }
}
