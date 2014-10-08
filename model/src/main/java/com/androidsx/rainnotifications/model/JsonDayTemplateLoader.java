package com.androidsx.rainnotifications.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class JsonDayTemplateLoader implements DayTemplateLoader{

    //TODO: The reader should be closed but.... When?

    private Reader reader;

    public JsonDayTemplateLoader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public List<DayTemplate> load() {
        return Arrays.asList(new GsonBuilder().registerTypeAdapter(DayTemplate.class, new DayTemplateDeserializer()).create().fromJson(reader, DayTemplate[].class));
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
