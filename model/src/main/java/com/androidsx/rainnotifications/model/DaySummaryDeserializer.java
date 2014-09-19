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
        DaySummary.WeatherWrapper primaryWeatherWrapper;
        DaySummary.WeatherWrapper secondaryWeatherWrapper;
        HashMap<WeatherPriority,DaySummary.WeatherWrapper> morning = new HashMap<WeatherPriority,DaySummary.WeatherWrapper>();
        HashMap<WeatherPriority,DaySummary.WeatherWrapper> afternoon = new HashMap<WeatherPriority,DaySummary.WeatherWrapper>();
        HashMap<WeatherPriority,DaySummary.WeatherWrapper> evening = new HashMap<WeatherPriority,DaySummary.WeatherWrapper>();
        HashMap<WeatherPriority,DaySummary.WeatherWrapper> night = new HashMap<WeatherPriority,DaySummary.WeatherWrapper>();
        HashMap<String,List<String>> messages = new HashMap<String,List<String>>();

        if(jsonObject.has(DayPeriod.morning.toString())) {
            JsonObject jsonMorning = (JsonObject) jsonObject.get(DayPeriod.morning.toString());
            primaryWeatherWrapper = context.deserialize(jsonMorning.get(WeatherPriority.primary.toString()), DaySummary.WeatherWrapper.class);
            morning.put(WeatherPriority.primary, primaryWeatherWrapper);
            if (jsonObject.has(WeatherPriority.secondary.toString())) {
                secondaryWeatherWrapper = context.deserialize(jsonMorning.get(WeatherPriority.secondary.toString()), DaySummary.WeatherWrapper.class);
                morning.put(WeatherPriority.secondary, secondaryWeatherWrapper);
            }
        }
        if(jsonObject.has(DayPeriod.afternoon.toString())) {
            JsonObject jsonAfternoon = (JsonObject) jsonObject.get(DayPeriod.afternoon.toString());
            primaryWeatherWrapper = context.deserialize(jsonAfternoon.get(WeatherPriority.primary.toString()), DaySummary.WeatherWrapper.class);
            afternoon.put(WeatherPriority.primary, primaryWeatherWrapper);
            if (jsonObject.has(WeatherPriority.secondary.toString())) {
                secondaryWeatherWrapper = context.deserialize(jsonAfternoon.get(WeatherPriority.secondary.toString()), DaySummary.WeatherWrapper.class);
                afternoon.put(WeatherPriority.secondary, secondaryWeatherWrapper);
            }
        }
        if(jsonObject.has(DayPeriod.evening.toString())) {
            JsonObject jsonEvening = (JsonObject) jsonObject.get(DayPeriod.evening.toString());
            primaryWeatherWrapper = context.deserialize(jsonEvening.get(WeatherPriority.primary.toString()), DaySummary.WeatherWrapper.class);
            evening.put(WeatherPriority.primary, primaryWeatherWrapper);
            if (jsonObject.has(WeatherPriority.secondary.toString())) {
                secondaryWeatherWrapper = context.deserialize(jsonEvening.get(WeatherPriority.secondary.toString()), DaySummary.WeatherWrapper.class);
                evening.put(WeatherPriority.secondary, secondaryWeatherWrapper);
            }
        }
        if(jsonObject.has(DayPeriod.night.toString())) {
            JsonObject jsonNight = (JsonObject) jsonObject.get(DayPeriod.night.toString());
            primaryWeatherWrapper = context.deserialize(jsonNight.get(WeatherPriority.primary.toString()), DaySummary.WeatherWrapper.class);
            night.put(WeatherPriority.primary, primaryWeatherWrapper);
            if (jsonObject.has(WeatherPriority.secondary.toString())) {
                secondaryWeatherWrapper = context.deserialize(jsonNight.get(WeatherPriority.secondary.toString()), DaySummary.WeatherWrapper.class);
                night.put(WeatherPriority.secondary, secondaryWeatherWrapper);
            }
        }

        daySummary.setMorning(morning);
        daySummary.setAfternoon(afternoon);
        daySummary.setEvening(evening);
        daySummary.setNight(night);

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

        return daySummary;
    }
}
