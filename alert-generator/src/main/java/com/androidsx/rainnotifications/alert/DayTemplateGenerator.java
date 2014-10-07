package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.DayTemplate;
import com.androidsx.rainnotifications.model.ForecastTable;

import java.util.ArrayList;
import java.util.List;

public class DayTemplateGenerator {

    private List<DayTemplate> dayTemplates;

    public DayTemplateGenerator(List<DayTemplate> dayTemplates) {
        this.dayTemplates = dayTemplates;
    }

    public DayTemplate getDayTemplate(Day day) {
        return getMostAccurate(getMatches(day));
    }

    public String generateMessage(ForecastTable forecastTable) {
        return generateMessage(forecastTable, null);
    }

    public String generateMessage(ForecastTable forecastTable, String defaultMessage) {
        Day day = Day.fromForecastTable(forecastTable);
        DayTemplate template = getDayTemplate(day);
        return template == null ? defaultMessage : template.resolveMessage(day);
    }

    private List<DayTemplate> getMatches(Day day) {
        List<DayTemplate> matches = new ArrayList<DayTemplate>();
        for (DayTemplate dayTemplate : dayTemplates) {
            if(dayTemplate.match(day)) matches.add(dayTemplate);
        }
        return matches;
    }

    private DayTemplate getMostAccurate(List<DayTemplate> matches) {
        //TODO: This is a temporary implementation.
        return matches.isEmpty() ? null : matches.get(matches.size() -1);
    }

    /*
    esto me lo guardo por si hago downgrade...

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
     */
}
