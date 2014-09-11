package com.androidsx.rainnotifications.alert;

import android.content.res.Resources;

import com.androidsx.rainnotifications.model.AlertMessage;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.util.UiUtil;

import org.joda.time.Period;

import java.util.Random;

public class DayMessageGenerator {
    private final Random random = new Random();
    private final Resources resources;

    public DayMessageGenerator(Resources resources) {
        this.resources = resources;
    }

    public String generateDayMessage(Weather currentWeather, ForecastTable forecastTable) {
        if (forecastTable.getForecasts().isEmpty()) {
            return "(Fallback) No changes expected for a while." //TODO: message that refers to no forecast expected in a few hours
                    + " At the moment, it is " + currentWeather;
        } else {
            return "";
        }
    }
}
