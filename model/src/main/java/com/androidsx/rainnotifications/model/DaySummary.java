package com.androidsx.rainnotifications.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DaySummary {
    private final Random random = new Random();
    private WeatherType morningWeather;
    private WeatherType afternoonWeather;
    private HashMap<String,List<String>> dayMessages;

    public WeatherType getMorningWeather() {
        return morningWeather;
    }

    public WeatherType getAfternoonWeather() {
        return afternoonWeather;
    }

    public String getDayMessage() {
        return pickRandom(dayMessages.get("en"), random);
    }

    @Override
    public String toString() {
        String output = "Day Messages for: \nMorning Weather: " + afternoonWeather + "\nAfternoon Weather: " + afternoonWeather + "\n";
        if(dayMessages.containsKey("en")) {
            for(String s : dayMessages.get("en")) {
                output += "Message (en): " + s + "\n";
            }
        }
        return output;
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }
}
