package com.androidsx.rainnotifications.alert;

import android.content.Context;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Setup { // TODO: Move this class to a more appropriate place.

    /**
     * Return an appropriate Reader for use with {@link com.androidsx.rainnotifications.model.JsonDayTemplateLoader#JsonDayTemplateLoader(java.io.Reader)}.
     * It can be the default or an updated version from a server.
     *
     * @return java.io.Reader for current DaySummary.json file dictionary
     */
    public static Reader getJsonDayTemplateReader(Context context) {
        // TODO: Implement logic for server updates.
        try {
            return new InputStreamReader(context.getResources().getAssets().open(Constants.DaySummary.DEFAULT_DAY_TEMPLATES_JSON_ASSET));
        } catch (IOException e) {
            throw new IllegalStateException("Can't open reader for file", e);
        }
    }
}
