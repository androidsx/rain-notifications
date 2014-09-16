package com.androidsx.rainnotifications.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * An alert, that has a level and a message.
 */
public class Alert {
    private final Random random = new Random();
    private AlertLevel alertLevel;
    private WeatherType fromType;
    private WeatherType toType;
    private HashMap<String,List<String>> alertMessages;
    private int dressedMascot;

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public WeatherType getFromType() {
        return this.fromType;
    }

    public WeatherType getToType() {
        return this.toType;
    }

    public int getDressedMascot() {
        return dressedMascot;
    }

    public void setDressedMascot(int dressedMascot) {
        this.dressedMascot = dressedMascot;
    }

    public AlertMessage getAlertMessage() {
        return new AlertMessage(pickRandom(alertMessages.get("en"), random));
    }

    @Override
    public String toString() {
        String output = "Alert Level: " + alertLevel + "\nfrom: " + fromType + "\nto: " + toType + "\n";
        if(alertMessages.containsKey("en")) {
            for(String s : alertMessages.get("en")) {
                output += "Message (en): " + s + "\n";
            }
        }
        if(alertMessages.containsKey("es")) {
            for (String s : alertMessages.get("es")) {
                output += "Message (es): " + s + "\n";
            }
        }
        return output;
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }
}
