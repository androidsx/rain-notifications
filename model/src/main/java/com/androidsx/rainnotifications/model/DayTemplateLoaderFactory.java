package com.androidsx.rainnotifications.model;

import android.content.Context;

import java.util.Arrays;

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
        return new MultiDayTemplateLoader(context, Arrays.asList(
                MultiDayTemplateLoader.FROM_MORNING_TEMPLATES_JSON_ASSET,
                MultiDayTemplateLoader.FROM_AFTERNOON_TEMPLATES_JSON_ASSET,
                MultiDayTemplateLoader.FROM_EVENING_TEMPLATES_JSON_ASSET));
    }
}
