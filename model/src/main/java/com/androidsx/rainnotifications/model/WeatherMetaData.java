package com.androidsx.rainnotifications.model;

public class WeatherMetaData {
    private final int temp; // in Fahrenheit

    public static class Builder {
        private int temp;

        public Builder temp(int temp) {
            this.temp = temp;
            return this;
        }

        public WeatherMetaData build() {
            return new WeatherMetaData(this);
        }
    }

    private WeatherMetaData(Builder builder) {
        temp = builder.temp;
    }

    public int getTemp() {
        return temp;
    }
}
