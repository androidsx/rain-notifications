package com.androidsx.rainnotifications.dailyclothes.model.clothesloader;

import com.androidsx.rainnotifications.model.Day;

public interface ClothesLoader {
    public void loadClothes(Day day, ClothesLoaderListener listener);
}
