package com.androidsx.rainnotifications.alert;

import android.content.Context;
import android.util.SparseArray;

import com.androidsx.rainnotifications.model.DaySummary;
import com.androidsx.rainnotifications.model.DaySummaryDeserializer;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.WeatherPriority;
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
    private DaySummaryPostProcessor daySummaryPostProcessor;

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
        daySummaryPostProcessor = new DaySummaryPostProcessor(Arrays.asList(gsonBuilder.create().fromJson(jsonReader, DaySummary[].class)));
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

        //TODO: Remove Workaround
        DaySummary perfectSummary = new DaySummary();

        HashMap<WeatherPriority,DaySummary.WeatherWrapper> morningWeather = new HashMap<WeatherPriority, DaySummary.WeatherWrapper>();
        morningWeather.put(WeatherPriority.primary, new DaySummary.WeatherWrapper(getMoreSignificantWeather(morningWeathers)));
        morningWeather.put(WeatherPriority.secondary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
        perfectSummary.setMorning(morningWeather);

        HashMap<WeatherPriority,DaySummary.WeatherWrapper> afternoonWeather = new HashMap<WeatherPriority, DaySummary.WeatherWrapper>();
        afternoonWeather.put(WeatherPriority.primary, new DaySummary.WeatherWrapper(getMoreSignificantWeather(afternoonWeathers)));
        afternoonWeather.put(WeatherPriority.secondary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
        perfectSummary.setMorning(afternoonWeather);

        HashMap<WeatherPriority,DaySummary.WeatherWrapper> undefinedWeather = new HashMap<WeatherPriority, DaySummary.WeatherWrapper>();
        undefinedWeather.put(WeatherPriority.primary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
        undefinedWeather.put(WeatherPriority.secondary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
        perfectSummary.setEvening(undefinedWeather);
        perfectSummary.setNight(undefinedWeather);

        DaySummary daySummary = daySummaryPostProcessor.getEquivalentDaySummary(perfectSummary);

        if(daySummary != null) {
            return daySummary;
        }
        else {
            HashMap<String, List<String>> messages = new HashMap<String, List<String>>();
            messages.put("en", Arrays.asList("Default"));
            perfectSummary.setMessages(messages);
            return perfectSummary;
        }
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

    private class DaySummaryPostProcessor {
        private static final int MAX_WHATEVER_LEVEL = 8; // This the maximum number of "Whatever" a DaySummary can have (4 DayPeriod * 2 DaySummary).
        private static final String KEY_SEPARATOR = "_";
        private List<String> keySeparatorList = Arrays.asList(KEY_SEPARATOR);
        private List<String> meaningfulWeatherTypeNames;
        private HashMap<String, DaySummary> sumariesMap;

        public DaySummaryPostProcessor(List<DaySummary> daySummaries) {
            sumariesMap = new HashMap<String, DaySummary>();
            meaningfulWeatherTypeNames = WeatherType.getMeaningfulWeatherTypeNames();

            SparseArray<ArrayList<DaySummary>> dispersedSumaries = getDispersedSummaries(daySummaries);

            for(int i = 0 ; i <= MAX_WHATEVER_LEVEL ; i++) {
                for(DaySummary daySummary : dispersedSumaries.get(i)) {
                    for(String key : getSuitableWeathersKeys(daySummary)) {
                        if(!sumariesMap.containsKey(key)) {
                            sumariesMap.put(key,daySummary);
                        }
                    }
                }
            }
        }

        public DaySummary getEquivalentDaySummary(DaySummary daySummary) {
            return sumariesMap.get(getDaySummaryWeatherKey(daySummary));
        }

        private String getDaySummaryWeatherKey(DaySummary daySummary) {
            StringBuilder builder = new StringBuilder();
            builder.append(daySummary.getMorning().get(WeatherPriority.primary) + KEY_SEPARATOR);
            builder.append(daySummary.getMorning().get(WeatherPriority.secondary) + KEY_SEPARATOR);
            builder.append(daySummary.getAfternoon().get(WeatherPriority.primary) + KEY_SEPARATOR);
            builder.append(daySummary.getAfternoon().get(WeatherPriority.secondary) + KEY_SEPARATOR);
            builder.append(daySummary.getEvening().get(WeatherPriority.primary) + KEY_SEPARATOR);
            builder.append(daySummary.getEvening().get(WeatherPriority.secondary) + KEY_SEPARATOR);
            builder.append(daySummary.getNight().get(WeatherPriority.primary) + KEY_SEPARATOR);
            builder.append(daySummary.getNight().get(WeatherPriority.secondary));

            return builder.toString();
        }

        private List<String> getSuitableWeathersKeys(DaySummary daySummary) {
            List<String> keys = Arrays.asList("");

            keys = addWeatherNamesToList(keys, daySummary.getMorning().get(WeatherPriority.primary).getType());
            keys = addTextToList(keys, keySeparatorList);
            keys = addWeatherNamesToList(keys, daySummary.getMorning().get(WeatherPriority.secondary).getType());
            keys = addTextToList(keys, keySeparatorList);
            keys = addWeatherNamesToList(keys, daySummary.getAfternoon().get(WeatherPriority.primary).getType());
            keys = addTextToList(keys, keySeparatorList);
            keys = addWeatherNamesToList(keys, daySummary.getAfternoon().get(WeatherPriority.secondary).getType());
            keys = addTextToList(keys, keySeparatorList);
            keys = addWeatherNamesToList(keys, daySummary.getEvening().get(WeatherPriority.primary).getType());
            keys = addTextToList(keys, keySeparatorList);
            keys = addWeatherNamesToList(keys, daySummary.getEvening().get(WeatherPriority.secondary).getType());
            keys = addTextToList(keys, keySeparatorList);
            keys = addWeatherNamesToList(keys, daySummary.getNight().get(WeatherPriority.primary).getType());
            keys = addTextToList(keys, keySeparatorList);
            keys = addWeatherNamesToList(keys, daySummary.getNight().get(WeatherPriority.secondary).getType());

            return keys;
        }

        private int getWhateverLevel(DaySummary daySummary) {
            int level = 0;

            if(daySummary.getMorning().get(WeatherPriority.primary).getType().equals(WeatherType.WHATEVER)) level++;
            if(daySummary.getMorning().get(WeatherPriority.secondary).getType().equals(WeatherType.WHATEVER)) level++;
            if(daySummary.getAfternoon().get(WeatherPriority.primary).getType().equals(WeatherType.WHATEVER)) level++;
            if(daySummary.getAfternoon().get(WeatherPriority.secondary).getType().equals(WeatherType.WHATEVER)) level++;
            if(daySummary.getEvening().get(WeatherPriority.primary).getType().equals(WeatherType.WHATEVER)) level++;
            if(daySummary.getEvening().get(WeatherPriority.secondary).getType().equals(WeatherType.WHATEVER)) level++;
            if(daySummary.getNight().get(WeatherPriority.primary).getType().equals(WeatherType.WHATEVER)) level++;
            if(daySummary.getNight().get(WeatherPriority.secondary).getType().equals(WeatherType.WHATEVER)) level++;

            return level;
        }

        private SparseArray<ArrayList<DaySummary>> getDispersedSummaries(List<DaySummary> daySummaries) {
            SparseArray<ArrayList<DaySummary>> dispersed = new SparseArray<ArrayList<DaySummary>>();

            for(int i = 0 ; i <= MAX_WHATEVER_LEVEL ; i++) {
                dispersed.append(i, new ArrayList<DaySummary>());
            }

            for(DaySummary daySummary : daySummaries) {
                dispersed.get(getWhateverLevel(daySummary)).add(daySummary);
            }

            return dispersed;
        }

        private List<String> addWeatherNamesToList(List<String> list, WeatherType weather) {
            if(weather.equals(WeatherType.WHATEVER)) {
                return addTextToList(list, meaningfulWeatherTypeNames);
            }
            else {
                return addTextToList(list, Arrays.asList(weather.toString()));
            }
        }

        private List<String> addTextToList(List<String> list, List<String> text) {
            List<String> newList = new ArrayList<String>();

            for (String current : list) {
                for (String add : text) {
                    newList.add(current + add);
                }
            }

            return newList;
        }
    }
}
