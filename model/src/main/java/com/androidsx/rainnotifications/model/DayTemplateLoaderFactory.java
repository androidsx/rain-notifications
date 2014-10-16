package com.androidsx.rainnotifications.model;

import android.content.Context;

public class DayTemplateLoaderFactory {

    /**
     * Return an appropriate {@link com.androidsx.rainnotifications.model.DayTemplateLoader}
     * It can use the default asset file or an updated version from a server.
     *
     * @param context
     * @return DayTemplateLoader
     */
    public static DayTemplateLoader getDayTemplateLoader(Context context) {
        // TODO: Implement logic for server updates.
        // TODO: Change loader.
        //return JsonDayTemplateLoader.fromAssets(context, JsonDayTemplateLoader.DEFAULT_DAY_TEMPLATES_JSON_ASSET);
        return new DevelopmentDayTemplateLoader(context);
    }
}
