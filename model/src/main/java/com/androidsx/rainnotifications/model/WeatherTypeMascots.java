package com.androidsx.rainnotifications.model;

import java.util.List;

public class WeatherTypeMascots {
    private WeatherType type;
    private List<String> dressedMascots;

    /** Empty constructor, for GSON. */
    public WeatherTypeMascots() {
    }

    /** Fully parameterized constructor, for tests. */
    public WeatherTypeMascots(WeatherType type, List<String> dressedMascots) {
        this.type = type;
        this.dressedMascots = dressedMascots;
    }

    public WeatherType getType() {
        return type;
    }

    public List<String> getDressedMascots() {
        return dressedMascots;
    }
}
