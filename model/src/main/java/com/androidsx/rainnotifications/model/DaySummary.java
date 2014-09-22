package com.androidsx.rainnotifications.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DaySummary {
    public static final int MAX_WEATHER_LEVEL = 9;

    private List<String> whateverTypes = Arrays.asList(WeatherType.CLEAR.toString(), WeatherType.RAIN.toString(), WeatherType.CLOUDY.toString(), WeatherType.PARTLY_CLOUDY.toString());
    private final Random random = new Random();

    private HashMap<WeatherPriority,WeatherWrapper> morning;
    private HashMap<WeatherPriority,WeatherWrapper> afternoon;
    private HashMap<WeatherPriority,WeatherWrapper> evening;
    private HashMap<WeatherPriority,WeatherWrapper> night;
    private HashMap<String, List<String>> messages;

    public void setMorning(HashMap<WeatherPriority, WeatherWrapper> morning) {
        this.morning = morning;
    }

    public void setAfternoon(HashMap<WeatherPriority, WeatherWrapper> afternoon) {
        this.afternoon = afternoon;
    }

    public void setEvening(HashMap<WeatherPriority, WeatherWrapper> evening) {
        this.evening = evening;
    }

    public void setNight(HashMap<WeatherPriority, WeatherWrapper> night) {
        this.night = night;
    }

    public HashMap<String, List<String>> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, List<String>> messages) {
        this.messages = messages;
    }

    public HashMap<WeatherPriority,WeatherWrapper> getMorning() {
        return morning;
    }

    public HashMap<WeatherPriority,WeatherWrapper> getAfternoon() {
        return afternoon;
    }

    public HashMap<WeatherPriority,WeatherWrapper> getEvening() {
        return evening;
    }

    public HashMap<WeatherPriority,WeatherWrapper> getNight() {
        return night;
    }

    public List<String> getSuitableWeathersKeys() {

        List<String> keys = Arrays.asList("");
        List<String> keySeparator = Arrays.asList("_");

        keys = addWeather(keys, morning.get(WeatherPriority.primary).getType());
        keys = addText(keys, keySeparator);
        keys = addWeather(keys, morning.get(WeatherPriority.secondary).getType());
        keys = addText(keys, keySeparator);
        keys = addWeather(keys, afternoon.get(WeatherPriority.primary).getType());
        keys = addText(keys, keySeparator);
        keys = addWeather(keys, afternoon.get(WeatherPriority.secondary).getType());
        keys = addText(keys, keySeparator);
        keys = addWeather(keys, evening.get(WeatherPriority.primary).getType());
        keys = addText(keys, keySeparator);
        keys = addWeather(keys, evening.get(WeatherPriority.secondary).getType());
        keys = addText(keys, keySeparator);
        keys = addWeather(keys, night.get(WeatherPriority.primary).getType());
        keys = addText(keys, keySeparator);
        keys = addWeather(keys, night.get(WeatherPriority.secondary).getType());

        return keys;
    }

    private List<String> addWeather(List<String> list, WeatherType weather) {

        if(weather.equals(WeatherType.WHATEVER)) {
            return addText(list, whateverTypes);
        }
        else {
            return addText(list, Arrays.asList(weather.toString()));
        }
    }

    private List<String> addText(List<String> list, List<String> text) {

        List<String> newList = new ArrayList<String>();

        for (String current : list) {
            for (String add : text) {
                newList.add(current + add);
            }
        }

        return newList;
    }

    public int getWhateverLevel() {
        int level = 0;

        if(morning.get(WeatherPriority.primary).getType().equals(WeatherType.WHATEVER)) level++;
        if(morning.get(WeatherPriority.secondary).getType().equals(WeatherType.WHATEVER)) level++;
        if(afternoon.get(WeatherPriority.primary).getType().equals(WeatherType.WHATEVER)) level++;
        if(afternoon.get(WeatherPriority.secondary).getType().equals(WeatherType.WHATEVER)) level++;
        if(evening.get(WeatherPriority.primary).getType().equals(WeatherType.WHATEVER)) level++;
        if(evening.get(WeatherPriority.secondary).getType().equals(WeatherType.WHATEVER)) level++;
        if(night.get(WeatherPriority.primary).getType().equals(WeatherType.WHATEVER)) level++;
        if(night.get(WeatherPriority.secondary).getType().equals(WeatherType.WHATEVER)) level++;

        return level;
    }

    public String getDayMessage() {
        return pickRandom(messages.get("en"), random);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Day Messages for: ");
        builder.append("\n");
        if (morning != null) {
            builder.append("Morning Primary Weather: " + morning.get(WeatherPriority.primary));
            builder.append("\n");
            if (morning.containsKey(WeatherPriority.secondary)) {
                builder.append("Morning Secondary Weather: " + morning.get(WeatherPriority.secondary));
                builder.append("\n");
            }
        }
        if (afternoon != null) {
            builder.append("Afternoon Primary Weather: " + afternoon.get(WeatherPriority.primary));
            builder.append("\n");
            if (afternoon.containsKey(WeatherPriority.secondary)) {
                builder.append("Afternoon Secondary Weather: " + afternoon.get(WeatherPriority.secondary));
                builder.append("\n");
            }
        }
        if (evening != null) {
            builder.append("Evening Primary Weather:" + evening.get(WeatherPriority.primary));
            builder.append("\n");
            if (evening.containsKey(WeatherPriority.secondary)) {
                builder.append("Evening Secondary Weather: " + evening.get(WeatherPriority.secondary));
                builder.append("\n");
            }
        }
        if (night != null) {
            builder.append("Night Primary Weather:" + night.get(WeatherPriority.primary));
            builder.append("\n");
            if (night.containsKey(WeatherPriority.secondary)) {
                builder.append("Night Secondary Weather: " + night.get(WeatherPriority.secondary));
                builder.append("\n");
            }
        }
        if(messages.containsKey("en")) {
            builder.append("\n");
            builder.append("Messages:");
            builder.append("\n");
            for(String s : messages.get("en")) {
                builder.append(String.format("Message (en): %s", s));
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }

    public static class WeatherWrapper {
        private WeatherType weatherType;

        public WeatherWrapper(WeatherType weatherType) {
            this.weatherType = weatherType;
        }

        public WeatherType getType() {
            return weatherType;
        }

        @Override
        public String toString() {
            return weatherType.toString();
        }
    }
}
