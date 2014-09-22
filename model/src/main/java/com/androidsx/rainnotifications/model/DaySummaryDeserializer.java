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
    public DaySummary deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final DaySummary daySummary = new DaySummary();

        HashMap<String,List<String>> messages = new HashMap<String,List<String>>();
        JsonObject jsonMessages = (JsonObject) jsonObject.get("messages");

        List<String> mList = new ArrayList<String>();
        if(jsonMessages.has("en")) {
            JsonArray messagesList = (JsonArray) jsonMessages.get("en");
            for(int i=0; i < messagesList.size(); i++) {
                mList.add(messagesList.get(i).getAsString());
            }
            messages.put("en", mList);
        }
        daySummary.setMessages(messages);

        daySummary.setMorning(getWeatherPriorityMap(context, jsonObject, DayPeriod.morning));
        daySummary.setAfternoon(getWeatherPriorityMap(context, jsonObject, DayPeriod.afternoon));
        daySummary.setEvening(getWeatherPriorityMap(context, jsonObject, DayPeriod.evening));
        daySummary.setNight(getWeatherPriorityMap(context, jsonObject, DayPeriod.night));

        return daySummary;
    }

    private HashMap<WeatherPriority,DaySummary.WeatherWrapper> getWeatherPriorityMap(JsonDeserializationContext context, JsonObject jsonObject, DayPeriod period) {

        HashMap<WeatherPriority,DaySummary.WeatherWrapper> periodWeatherPriorityMap = new HashMap<WeatherPriority,DaySummary.WeatherWrapper>();
        periodWeatherPriorityMap.put(WeatherPriority.primary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));
        periodWeatherPriorityMap.put(WeatherPriority.secondary, new DaySummary.WeatherWrapper(WeatherType.UNDEFINED));

        if(jsonObject.has(period.toString())) {
            JsonObject jsonPeriod = (JsonObject) jsonObject.get(period.toString());

            periodWeatherPriorityMap.put(WeatherPriority.primary, (DaySummary.WeatherWrapper) context.deserialize(jsonPeriod.get(WeatherPriority.primary.toString()), DaySummary.WeatherWrapper.class));

            if (jsonPeriod.has(WeatherPriority.secondary.toString())) {
                periodWeatherPriorityMap.put(WeatherPriority.secondary, (DaySummary.WeatherWrapper) context.deserialize(jsonPeriod.get(WeatherPriority.secondary.toString()), DaySummary.WeatherWrapper.class));
            }
        }

        return periodWeatherPriorityMap;
    }
}
