package com.androidsx.rainnotifications.forecastapislibrary;

public class WeatherClientException extends Exception {

    public WeatherClientException(String detailMessage) {
        super(detailMessage);
    }
    public WeatherClientException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
