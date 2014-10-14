package com.androidsx.rainnotifications.alert;

import android.content.Context;

import com.androidsx.rainnotifications.model.DayTemplateLoader;
import com.androidsx.rainnotifications.model.JsonDayTemplateLoader;

public class Setup { // TODO: Move this class to a more appropriate place.

    /**
     * Return an appropriate {@link com.androidsx.rainnotifications.model.DayTemplateLoader} for use with
     * {@link com.androidsx.rainnotifications.alert.DayTemplateGenerator#DayTemplateGenerator(com.androidsx.rainnotifications.model.DayTemplateLoader)}.
     * It can use the default asset file or an updated version from a server.
     *
     * @param context
     * @return DayTemplateLoader
     */
    public static DayTemplateLoader getDayTemplateLoader(Context context) {
        // TODO: Implement logic for server updates.
        return JsonDayTemplateLoader.fromAssets(context, Constants.DayTemplate.DEFAULT_DAY_TEMPLATES_JSON_ASSET);
    }
}
