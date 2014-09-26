package com.androidsx.rainnotifications.alert;

import android.content.Context;
import android.util.SparseArray;

import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.DaySummary;
import com.androidsx.rainnotifications.model.DaySummaryDeserializer;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.WeatherPriority;
import com.androidsx.rainnotifications.model.WeatherType;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class DaySummaryGenerator {
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
        return daySummaryPostProcessor.getClosestDaySummary(DaySummary.fromForecastTable(forecastTable));
    }

    private class DaySummaryPostProcessor {
        private List<String> meaningfulWeatherTypeNames;
        private HashMap<String, DaySummary> sumariesMap;

        /**
         * This the maximum number of "Whatever" a DaySummary can have.
         */
        private final int MAX_WHATEVER_LEVEL = DayPeriod.values().length * WeatherPriority.values().length;

        public DaySummaryPostProcessor(List<DaySummary> daySummaries) {
            sumariesMap = new HashMap<String, DaySummary>();
            meaningfulWeatherTypeNames = WeatherType.getMeaningfulWeatherTypeNames();

            SparseArray<ArrayList<DaySummary>> dispersedSumaries = getDispersedSummaries(daySummaries);

            for (int i = 0 ; i <= MAX_WHATEVER_LEVEL ; i++) {
                for (DaySummary daySummary : dispersedSumaries.get(i)) {
                    for (String key : getSuitableWeathersKeys(daySummary)) {
                        if (!sumariesMap.containsKey(key)) {
                            sumariesMap.put(key,daySummary);
                        }
                    }
                }
            }
        }

        public DaySummary getClosestDaySummary(DaySummary daySummary) {
            Timber.d("getClosestDaySummary for: " + daySummary);
            DaySummary onMapSummary = getDaySummaryFromMap(daySummary);

            while (onMapSummary == null) {
                if(downgradeDaySummary(daySummary)) {
                    onMapSummary = getDaySummaryFromMap(daySummary);
                }
                else {
                    Timber.d("Can't find suitable summary");
                    // Set default message
                    HashMap<String, List<String>> messages = new HashMap<String, List<String>>();
                    messages.put("en", Arrays.asList("Default")); //TODO: Review this message.
                    daySummary.setMessages(messages);
                    onMapSummary = daySummary;

                    break;
                }
            }

            return onMapSummary;
        }

        private DaySummary getDaySummaryFromMap(DaySummary daySummary) {
            return sumariesMap.get(getDaySummaryWeatherKey(daySummary));
        }

        private boolean downgradeDaySummary(DaySummary daySummary) {

            if (!daySummary.getWeatherWrapper(DayPeriod.night, WeatherPriority.secondary).getType().equals(WeatherType.UNDEFINED)) {
                Timber.d("remove night secondary");
                daySummary.setWeatherWrapper(DayPeriod.night, WeatherPriority.secondary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
                return true;
            }

            if (!daySummary.getWeatherWrapper(DayPeriod.night, WeatherPriority.primary).getType().equals(WeatherType.UNDEFINED)) {
                Timber.d("remove night primary");
                daySummary.setWeatherWrapper(DayPeriod.night, WeatherPriority.primary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
                return true;
            }

            if (!daySummary.getWeatherWrapper(DayPeriod.evening, WeatherPriority.secondary).getType().equals(WeatherType.UNDEFINED)) {
                Timber.d("remove evening secondary");
                daySummary.setWeatherWrapper(DayPeriod.evening, WeatherPriority.secondary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
                return true;
            }

            if (!daySummary.getWeatherWrapper(DayPeriod.evening, WeatherPriority.primary).getType().equals(WeatherType.UNDEFINED)) {
                Timber.d("remove evening primary");
                daySummary.setWeatherWrapper(DayPeriod.evening, WeatherPriority.primary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
                return true;
            }

            if (!daySummary.getWeatherWrapper(DayPeriod.afternoon, WeatherPriority.secondary).getType().equals(WeatherType.UNDEFINED)) {
                Timber.d("remove afternoon secondary");
                daySummary.setWeatherWrapper(DayPeriod.afternoon, WeatherPriority.secondary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
                return true;
            }

            if (!daySummary.getWeatherWrapper(DayPeriod.morning, WeatherPriority.secondary).getType().equals(WeatherType.UNDEFINED)) {
                Timber.d("remove morning secondary");
                daySummary.setWeatherWrapper(DayPeriod.morning, WeatherPriority.secondary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
                return true;
            }

            if (!daySummary.getWeatherWrapper(DayPeriod.afternoon, WeatherPriority.primary).getType().equals(WeatherType.UNDEFINED)) {
                Timber.d("remove afternoon primary");
                daySummary.setWeatherWrapper(DayPeriod.afternoon, WeatherPriority.primary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
                return true;
            }

            if (!daySummary.getWeatherWrapper(DayPeriod.morning, WeatherPriority.primary).getType().equals(WeatherType.UNDEFINED)) {
                Timber.d("remove morning primary");
                daySummary.setWeatherWrapper(DayPeriod.morning, WeatherPriority.primary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
                return true;
            }

            return false;
        }

        private String getDaySummaryWeatherKey(DaySummary daySummary) {
            StringBuilder builder = new StringBuilder();

            for (DayPeriod period : DayPeriod.values()) {
                for (WeatherPriority priority : WeatherPriority.values()) {
                    builder.append(daySummary.getWeatherWrapper(period, priority).getType());
                }
            }

            return builder.toString();
        }

        private List<String> getSuitableWeathersKeys(DaySummary daySummary) {
            List<String> keys = Arrays.asList("");

            for (DayPeriod period : DayPeriod.values()) {
                for (WeatherPriority priority : WeatherPriority.values()) {
                    keys = addWeatherNamesToList(keys, daySummary.getWeatherWrapper(period, priority).getType());
                }
            }

            return keys;
        }

        private int getWhateverLevel(DaySummary daySummary) {
            int level = 0;

            for (DayPeriod period : DayPeriod.values()) {
                for (WeatherPriority priority : WeatherPriority.values()) {
                   if (daySummary.getWeatherWrapper(period, priority).getType().equals(WeatherType.WHATEVER)) level++;
                }
            }

            return level;
        }

        private SparseArray<ArrayList<DaySummary>> getDispersedSummaries(List<DaySummary> daySummaries) {
            SparseArray<ArrayList<DaySummary>> dispersed = new SparseArray<ArrayList<DaySummary>>();

            for (int i = 0 ; i <= MAX_WHATEVER_LEVEL ; i++) {
                dispersed.append(i, new ArrayList<DaySummary>());
            }

            for (DaySummary daySummary : daySummaries) {
                dispersed.get(getWhateverLevel(daySummary)).add(daySummary);
            }

            return dispersed;
        }

        private List<String> addWeatherNamesToList(List<String> list, WeatherType weather) {
            if (weather.equals(WeatherType.WHATEVER)) {
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
