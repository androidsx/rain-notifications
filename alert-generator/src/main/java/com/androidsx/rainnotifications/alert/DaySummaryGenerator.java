package com.androidsx.rainnotifications.alert;

import android.util.SparseArray;

import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.DaySummary;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.WeatherPriority;
import com.androidsx.rainnotifications.model.WeatherType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class DaySummaryGenerator {
    private DaySummaryPostProcessor daySummaryPostProcessor;

    public DaySummaryGenerator(List<DaySummary> daySummaries) {
        daySummaryPostProcessor = new DaySummaryPostProcessor(daySummaries);
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
            meaningfulWeatherTypeNames = new ArrayList<String>();

            for (WeatherType type : WeatherType.getMeaningfulWeatherTypes()) {
                meaningfulWeatherTypeNames.add(type.toString());
            }

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
                if(daySummary.downgrade()) {
                    onMapSummary = getDaySummaryFromMap(daySummary);
                }
                else {
                    Timber.d("Can't find suitable summary");
                    onMapSummary = daySummary;
                }
            }

            return onMapSummary;
        }

        private DaySummary getDaySummaryFromMap(DaySummary daySummary) {
            return sumariesMap.get(getDaySummaryWeatherKey(daySummary));
        }

        private String getDaySummaryWeatherKey(DaySummary daySummary) {
            StringBuilder builder = new StringBuilder();

            for (DayPeriod period : DayPeriod.values()) {
                for (WeatherPriority priority : WeatherPriority.values()) {
                    builder.append(daySummary.getWeatherType(period, priority));
                }
            }

            return builder.toString();
        }

        private List<String> getSuitableWeathersKeys(DaySummary daySummary) {
            List<String> keys = Arrays.asList("");

            for (DayPeriod period : DayPeriod.values()) {
                for (WeatherPriority priority : WeatherPriority.values()) {
                    keys = addWeatherNamesToList(keys, daySummary.getWeatherType(period, priority));
                }
            }

            return keys;
        }

        private int getWhateverLevel(DaySummary daySummary) {
            int level = 0;

            for (DayPeriod period : DayPeriod.values()) {
                for (WeatherPriority priority : WeatherPriority.values()) {
                   if (daySummary.getWeatherType(period, priority).equals(WeatherType.WHATEVER)) level++;
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
