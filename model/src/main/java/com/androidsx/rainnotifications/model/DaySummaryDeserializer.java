package com.androidsx.rainnotifications.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DaySummaryDeserializer implements JsonDeserializer {
    @Override
    public DaySummaryV2 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        DaySummaryV2.DaySummaryBuilder builder = new DaySummaryV2.DaySummaryBuilder();

        for (DayPeriod period : DayPeriod.values()) {
            if (jsonObject.has(period.toString())) {
                JsonObject jsonPeriod = (JsonObject) jsonObject.get(period.toString());
                for (WeatherPriority priority : WeatherPriority.values()) {
                    if (jsonPeriod.has(priority.toString())) {
                        builder.setWeatherType(period, priority, (WeatherType) context.deserialize(jsonPeriod.getAsJsonObject(priority.toString()).get("weatherType"), WeatherType.class));
                    }
                }
            }
        }

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
