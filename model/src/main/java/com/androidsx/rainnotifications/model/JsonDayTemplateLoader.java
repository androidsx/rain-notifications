package com.androidsx.rainnotifications.model;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class JsonDayTemplateLoader implements DayTemplateLoader{

    private Reader reader;

    public JsonDayTemplateLoader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public List<DayTemplate> load() {
        return new ArrayList<DayTemplate>();
    }

    /*

    public class DaySummaryDeserializer implements JsonDeserializer {

    public static List<Day> deserializeDaySummaryDictionary(Reader dictionaryReader) {
        return Arrays.asList(new GsonBuilder().registerTypeAdapter(Day.class, new DaySummaryDeserializer()).create()
                .fromJson(dictionaryReader, Day[].class));
    }

    @Override
    public Day deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        Day.DaySummaryBuilder builder = new Day.DaySummaryBuilder();

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
    */
}
