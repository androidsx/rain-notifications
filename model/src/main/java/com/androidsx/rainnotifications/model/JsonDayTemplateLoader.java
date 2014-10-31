package com.androidsx.rainnotifications.model;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class JsonDayTemplateLoader implements DayTemplateLoader{

    private Context applicationContext;
    private String assetFileName;
    private File file;

    public static JsonDayTemplateLoader fromAssets(Context context, String assetFileName) {
        JsonDayTemplateLoader loader = new JsonDayTemplateLoader();
        loader.applicationContext = context.getApplicationContext();
        loader.assetFileName = assetFileName;
        return loader;
    }

    public static JsonDayTemplateLoader fromFile(File file) {
        JsonDayTemplateLoader loader = new JsonDayTemplateLoader();
        loader.file = file;
        return loader;
    }

    private JsonDayTemplateLoader() {
        // Non-instantiable
    }

    private Reader getReader() throws IOException {
        if(assetFileName != null) {
            return new InputStreamReader(applicationContext.getAssets().open(assetFileName));
        }
        else {
            return new InputStreamReader(new FileInputStream(file));
        }
    }

    @Override
    public List<DayTemplate> load() {
        Reader reader = null;

        try {
            reader = getReader();
        } catch (IOException e) {
            //TODO: Throw this Exception and add code for handle it.
            Timber.e("IOException opening reader");
            return new ArrayList<DayTemplate>();
        }

        List<DayTemplate> templates = Arrays.asList(new GsonBuilder().registerTypeAdapter(DayTemplate.class, new DayTemplateDeserializer()).create().fromJson(reader, DayTemplate[].class));

        try {
            reader.close();
        } catch (IOException e) {
            // Eat it.
            Timber.w("IOException closing reader");
        }
        return templates;
    }

    private class DayTemplateDeserializer implements JsonDeserializer{

        @Override
        public DayTemplate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            final JsonObject jsonObject = json.getAsJsonObject();

            DayTemplate.DayTemplateBuilder builder = new DayTemplate.DayTemplateBuilder();

            for (DayPeriod period : DayPeriod.values()) {
                if (jsonObject.has(period.toString())) {
                    JsonObject jsonPeriod = (JsonObject) jsonObject.get(period.toString());
                    for (WeatherPriority priority : WeatherPriority.values()) {
                        if (jsonPeriod.has(priority.toString())) {
                            JsonElement element = jsonPeriod.getAsJsonObject(priority.toString()).get("weatherType");
                            DayTemplate.DayTemplateJokerType joker = context.deserialize(element, DayTemplate.DayTemplateJokerType.class);

                            if(joker != null) {
                                builder.setWeatherType(period, priority, joker);
                            }
                            else {
                                builder.setWeatherType(period, priority, (WeatherType) context.deserialize(element, WeatherType.class));
                            }
                        }
                    }
                }
            }

            //TODO: ReImplement with Multilanguage support.
            HashMap<String,List<String>> messages = new HashMap<String,List<String>>();
            JsonObject jsonMessages = (JsonObject) jsonObject.get("messages");

            List<String> mList = new ArrayList<String>();
            if (jsonMessages.has("en")) {
                JsonArray messagesList = (JsonArray) jsonMessages.get("en");
                for (int i=0; i < messagesList.size(); i++) {
                    mList.add(messagesList.get(i).getAsString());
                }
                messages.put("en", mList);
            }

            builder.setMessages(messages);

            return builder.build();
        }
    }
}
