package com.androidsx.rainnotifications.dailyclothes.model.clothesloader.local;

import android.content.Context;
import android.os.Handler;

import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.ClothesLoader;
import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.ClothesLoaderException;
import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.ClothesLoaderListener;
import com.androidsx.rainnotifications.model.DailyWeatherWrapper;
import com.androidsx.rainnotifications.model.WeatherWrapper;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LocalClothesLoader implements ClothesLoader {

    private static final int SIMULATE_ASYNC_TASK_DELAY = 200;
    private static final String PATH_ASSETS = "file:///android_asset/";
    private static final String PATH_CLOTHES = "clothes/";


    @Override
    public void loadClothes(Context context, DailyWeatherWrapper dailyWeather, ClothesLoaderListener listener) {
        // TODO: At this time temperature is not taken into account

        Timber.d("LOADING CLOTHES FOR " + dailyWeather.getWeatherType()
                + " Min: " + dailyWeather.getMinTemperature(WeatherWrapper.TemperatureScale.CELSIUS)
                + " Max: " + dailyWeather.getMaxTemperature(WeatherWrapper.TemperatureScale.CELSIUS));

        try {
            String weatherClothesPath = PATH_CLOTHES + dailyWeather.getWeatherType().toString().toLowerCase();
            String[] clothesPaths = context.getAssets().list(weatherClothesPath);

            final List<Clothes> clothesList = new ArrayList<Clothes>();
            for (String path : clothesPaths) {
                clothesList.add(new LocalClothes(PATH_ASSETS + weatherClothesPath + "/" + path));
            }

            simulateAsyncLoad(listener, clothesList);

        } catch (Exception e) {
            Timber.e(e, "Failed to load clothes from assets");
            simulateAsyncLoad(listener, null);
        }
    }

    private void simulateAsyncLoad(final ClothesLoaderListener listener, final List<Clothes> clothesList) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(clothesList == null) {
                    listener.onClothesLoaderFailure(new ClothesLoaderException("Null assets list"));
                }
                else if(clothesList.isEmpty()) {
                    listener.onClothesLoaderFailure(new ClothesLoaderException("Empty assets list"));
                }
                else {
                    listener.onClothesLoaderSuccess(clothesList);
                }
            }
        }, SIMULATE_ASYNC_TASK_DELAY);
    }
}
