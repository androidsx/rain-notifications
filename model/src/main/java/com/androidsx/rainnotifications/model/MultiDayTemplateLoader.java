package com.androidsx.rainnotifications.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class MultiDayTemplateLoader implements DayTemplateLoader{

    public static final String FROM_MORNING_TEMPLATES_JSON_ASSET = "fromMorningTemplates.json";
    public static final String FROM_AFTERNOON_TEMPLATES_JSON_ASSET = "fromAfternoonTemplates.json";
    public static final String FROM_EVENING_TEMPLATES_JSON_ASSET = "fromEveningTemplates.json";

    private Context applicationContext;
    private List<String> assets;

    public MultiDayTemplateLoader(Context context, List<String> assets) {
        this.applicationContext = context.getApplicationContext();
        this.assets = assets;
    }

    @Override
    public List<DayTemplate> load() {
        List<DayTemplate> templates = new ArrayList<DayTemplate>();
        for (String asset : assets) {
            templates.addAll(JsonDayTemplateLoader.fromAssets(applicationContext, asset).load());
        }
        return templates;
    }
}
