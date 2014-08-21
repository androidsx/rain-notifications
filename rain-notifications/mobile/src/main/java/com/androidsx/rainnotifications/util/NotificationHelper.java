package com.androidsx.rainnotifications.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.androidsx.rainnotifications.ForecastMobile;
import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;

/*
 * This helper class is for notify the user by notifications if a significant weather change
 * is near to occur.
 */

public class NotificationHelper {

    private NotificationHelper() {
        //No-instantiate
    }

    public static void sendNotification(Context context, int id, int currentWeatherIcon, int forecastIcon, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(currentWeatherIcon);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), forecastIcon));
        mBuilder.setContentTitle(context.getString(R.string.app_name));
        mBuilder.setContentText(message);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);

        Intent mIntent = new Intent(context, ForecastMobile.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, mIntent, 0);

        mBuilder.setContentIntent(pi);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
    }

    public static String getOptimumMessage(Weather currentWeather, Forecast forecast) {
        if((currentWeather.getType() == WeatherType.CLEAR_DAY || currentWeather.getType() == WeatherType.CLEAR_NIGHT)
                && (forecast.getForecastedWeather().getType() != WeatherType.CLEAR_DAY || forecast.getForecastedWeather().getType() != WeatherType.CLEAR_NIGHT)) {
            if(forecast.getForecastedWeather().getType() == WeatherType.PARTLY_CLOUDY_DAY || forecast.getForecastedWeather().getType() == WeatherType.PARTLY_CLOUDY_NIGHT) {
                return "Probably, the sun is gonna be gone in minutes!";
            } else if(forecast.getForecastedWeather().getType() == WeatherType.CLOUDY) {
                return "The sun is gonna be gone in minutes!";
            } else if(forecast.getForecastedWeather().getType() == WeatherType.RAIN) {
                return "Grab the umbrella! In minutes probably rain!";
            }
        } else if((currentWeather.getType() == WeatherType.PARTLY_CLOUDY_DAY || currentWeather.getType() == WeatherType.PARTLY_CLOUDY_NIGHT)
                && (forecast.getForecastedWeather().getType() != WeatherType.PARTLY_CLOUDY_DAY || forecast.getForecastedWeather().getType() != WeatherType.PARTLY_CLOUDY_NIGHT)) {
            if(forecast.getForecastedWeather().getType() == WeatherType.CLEAR_DAY || forecast.getForecastedWeather().getType() == WeatherType.CLEAR_NIGHT) {
                return "The sun will shine soon! Leave the jacket!";
            } else if(forecast.getForecastedWeather().getType() == WeatherType.CLOUDY) {
                return "The sun will not be back! Do not forget your jacket, you may cool!";
            } else if(forecast.getForecastedWeather().getType() == WeatherType.RAIN) {
                return "Grab the umbrella! In minutes probably rain!";
            }
        } else if(currentWeather.getType() == WeatherType.CLOUDY && forecast.getForecastedWeather().getType() != WeatherType.CLOUDY) {
            if(forecast.getForecastedWeather().getType() == WeatherType.CLEAR_DAY || forecast.getForecastedWeather().getType() == WeatherType.CLEAR_NIGHT) {
                return "The sun will shine soon! Leave the jacket!";
            } else if(forecast.getForecastedWeather().getType() == WeatherType.PARTLY_CLOUDY_DAY || forecast.getForecastedWeather().getType() != WeatherType.PARTLY_CLOUDY_NIGHT) {
                return "The sun will be back! Don't worry if you have forgotten your jacket!";
            } else if(forecast.getForecastedWeather().getType() == WeatherType.RAIN) {
                return "Grab the umbrella! In minutes probably rain!";
            }
        }
        return "Now is " + currentWeather + ". We don't expect changes!";
    }
}
