package com.androidsx.rainnotifications.alert;

import android.util.SparseArray;

import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.DayPeriod;
import com.androidsx.rainnotifications.model.DayTemplate;
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

    public DaySummaryGenerator(List<DayTemplate> dayTemplates) {

    }

    /*
    public DaySummaryGenerator(List<Day> daySummaries) {
        daySummaryPostProcessor = new DaySummaryPostProcessor(daySummaries);
    }
    */

    public Day getDaySummary(ForecastTable forecastTable) {
        return daySummaryPostProcessor.getClosestDaySummary(Day.fromForecastTable(forecastTable));
    }

    protected DaySummaryPostProcessor getPostProcessor() {
        return daySummaryPostProcessor;
    }

    protected class DaySummaryPostProcessor {
        private List<String> meaningfulWeatherTypeNames;
        private HashMap<String, Day> sumariesMap;

        /**
         * This the maximum number of "Whatever" a DaySummary can have.
         */
        private final int MAX_WHATEVER_LEVEL = DayPeriod.values().length * WeatherPriority.values().length;

        public DaySummaryPostProcessor(List<Day> daySummaries) {
            sumariesMap = new HashMap<String, Day>();
            meaningfulWeatherTypeNames = new ArrayList<String>();

            for (WeatherType type : WeatherType.getMeaningfulWeatherTypes()) {
                meaningfulWeatherTypeNames.add(type.toString());
            }

            SparseArray<ArrayList<Day>> dispersedSumaries = getDispersedSummaries(daySummaries);

            for (int i = 0 ; i <= MAX_WHATEVER_LEVEL ; i++) {
                for (Day day : dispersedSumaries.get(i)) {
                    for (String key : getSuitableWeathersKeys(day)) {
                        if (!sumariesMap.containsKey(key)) {
                            sumariesMap.put(key, day);
                        }
                    }
                }
            }
        }

        public Day getClosestDaySummary(Day day) {
            Timber.d("getClosestDaySummary for: " + day);
            Day onMapSummary = getDaySummary(day);

            while (onMapSummary == null) {
                if(day.downgrade()) {
                    onMapSummary = getDaySummary(day);
                }
                else {
                    Timber.d("Can't find suitable summary");
                    onMapSummary = day;
                }
            }

            return onMapSummary;
        }

        public Day getDaySummary(Day day) {
            return sumariesMap.get(getDaySummaryWeatherKey(day));
        }

        private String getDaySummaryWeatherKey(Day day) {
            StringBuilder builder = new StringBuilder();

            for (DayPeriod period : DayPeriod.values()) {
                for (WeatherPriority priority : WeatherPriority.values()) {
                    builder.append(day.getWeatherType(period, priority));
                }
            }

            return builder.toString();
        }

        private List<String> getSuitableWeathersKeys(Day day) {
            List<String> keys = Arrays.asList("");

            for (DayPeriod period : DayPeriod.values()) {
                for (WeatherPriority priority : WeatherPriority.values()) {
                    keys = addWeatherNamesToList(keys, day.getWeatherType(period, priority));
                }
            }

            return keys;
        }

        private SparseArray<ArrayList<Day>> getDispersedSummaries(List<Day> daySummaries) {
            SparseArray<ArrayList<Day>> dispersed = new SparseArray<ArrayList<Day>>();

            /*
            for (int i = 0 ; i <= MAX_WHATEVER_LEVEL ; i++) {
                dispersed.append(i, new ArrayList<Day>());
            }

            for (Day day : daySummaries) {
                dispersed.get(day.getWeatherLevel(WeatherType.WHATEVER)).add(day);
            }
            */

            return dispersed;
        }

        private List<String> addWeatherNamesToList(List<String> list, WeatherType weather) {
            /*
            if (weather.equals(WeatherType.WHATEVER)) {
                return addTextToList(list, meaningfulWeatherTypeNames);
            }
            else {
                return addTextToList(list, Arrays.asList(weather.toString()));
            }
            */
            return null;
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
