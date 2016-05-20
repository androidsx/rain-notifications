package com.androidsx.rainnotifications.dailyclothes.model.clothesloader.network;

import android.content.Context;

import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.ClothesLoader;
import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.ClothesLoaderListener;
import com.androidsx.rainnotifications.model.DailyWeatherWrapper;

public class NetworkClothesLoader implements ClothesLoader{

    @Override
    public void loadClothes(Context context, DailyWeatherWrapper dailyWeather, ClothesLoaderListener listener) {
        throw new IllegalArgumentException("Unimplemented NetworkClothesLoader");
    }
}
