package com.androidsx.rainnotifications.dailyclothes.model.clothesloader;

import android.content.Context;

import com.androidsx.rainnotifications.model.DailyWeatherWrapper;

public interface ClothesLoader {
    public void loadClothes(Context context, DailyWeatherWrapper dailyWeather, ClothesLoaderListener listener);
}
