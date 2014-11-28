package com.androidsx.rainnotifications.dailyclothes.model.clothesloader.network;

import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.ClothesLoader;
import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.ClothesLoaderListener;
import com.androidsx.rainnotifications.model.Day;

public class NetworkClothesLoader implements ClothesLoader{

    @Override
    public void loadClothes(Day day, ClothesLoaderListener listener) {
        throw new IllegalArgumentException("Unimplemented NetworkClothesLoader");
    }
}
