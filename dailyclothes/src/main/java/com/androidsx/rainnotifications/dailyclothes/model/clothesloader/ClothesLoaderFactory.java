package com.androidsx.rainnotifications.dailyclothes.model.clothesloader;

import android.content.Context;

import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.local.LocalClothesLoader;
import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.network.NetworkClothesLoader;
import com.androidsx.rainnotifications.model.DailyWeatherWrapper;

public abstract class ClothesLoaderFactory {

    private static final ClothesLoaderType TYPE = ClothesLoaderType.LOCAL_LOADER;

    public static void getClothes(Context context, DailyWeatherWrapper dailyWeather, ClothesLoaderListener listener) {

        final ClothesLoader clothesLoader;
        switch (TYPE) {
            case NETWORK_LOADER: clothesLoader = new NetworkClothesLoader();
                break;
            case LOCAL_LOADER: clothesLoader = new LocalClothesLoader();
                break;
            default: throw new IllegalArgumentException("Unsupported type: " + TYPE);
        }

        clothesLoader.loadClothes(context, dailyWeather, listener);
    }
}
