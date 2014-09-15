package com.androidsx.rainnotifications.model;

import com.androidsx.rainnotifications.model.util.UiUtil;

import org.joda.time.Interval;
import org.joda.time.Period;

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
    private Interval interval;

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public HashMap<String,List<String>> getAlertMessages() {
        return alertMessages;
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

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public AlertMessage getAlertMessage() {
        return new AlertMessage(String.format(pickRandom(getAlertMessages().get("en"), random),
                UiUtil.getDebugOnlyPeriodFormatter().print(new Period(interval))));
    }

    @Override
    public String toString() {
        String output = "";
        output += "Alert Level: " + alertLevel + "\nfrom: " + fromType + "\nto: " + toType + "\n";
        if(getAlertMessages().containsKey("en")) {
            for(String s : getAlertMessages().get("en")) {
                output += "Message (en): " + s + "\n";
            }
        }
        if(getAlertMessages().containsKey("es")) {
            for (String s : getAlertMessages().get("es")) {
                output += "Message (es): " + s + "\n";
            }
        }
        return output;
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }
}
