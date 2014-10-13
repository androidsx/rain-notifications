package com.androidsx.rainnotifications.alert;

import android.content.Context;

import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.DayTemplate;
import com.androidsx.rainnotifications.model.DayTemplateLoader;
import com.androidsx.rainnotifications.model.ForecastTable;

import java.util.ArrayList;
import java.util.List;

public class DayTemplateGenerator {

    private List<DayTemplate> dayTemplates;

    public DayTemplateGenerator(DayTemplateLoader loader) {
        this.dayTemplates = loader.load();
    }

    public DayTemplate getDayTemplate(Day day) {
        return getMostAccurate(getMatches(day));
    }

    public String generateMessage(Context context, ForecastTable forecastTable) {
        return generateMessage(context, forecastTable, null);
    }

    public String generateMessage(Context context, ForecastTable forecastTable, String defaultMessage) {
        Day day = new Day(forecastTable);
        DayTemplate template = getDayTemplate(day);
        return template == null ? defaultMessage : template.resolveMessage(context,day);
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
}
