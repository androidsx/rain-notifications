package com.androidsx.rainnotifications.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class DevelopmentDayTemplateLoader implements DayTemplateLoader{

    public static final String FROM_MORNING_TEMPLATES_JSON_ASSET = "fromMorningTemplates.json";
    public static final String FROM_AFTERNOON_TEMPLATES_JSON_ASSET = "fromAfternoonTemplates.json";
    public static final String FROM_EVENING_TEMPLATES_JSON_ASSET = "fromEveningTemplates.json";

    private Context applicationContext;

    public DevelopmentDayTemplateLoader(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public List<DayTemplate> load() {
        List<DayTemplate> templates = new ArrayList<DayTemplate>();

        templates.addAll(JsonDayTemplateLoader.fromAssets(applicationContext, FROM_EVENING_TEMPLATES_JSON_ASSET).load());
        templates.addAll(JsonDayTemplateLoader.fromAssets(applicationContext, FROM_AFTERNOON_TEMPLATES_JSON_ASSET).load());
        templates.addAll(JsonDayTemplateLoader.fromAssets(applicationContext, FROM_MORNING_TEMPLATES_JSON_ASSET).load());

        return templates;
    }
}
