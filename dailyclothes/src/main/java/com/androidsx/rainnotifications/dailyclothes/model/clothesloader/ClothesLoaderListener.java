package com.androidsx.rainnotifications.dailyclothes.model.clothesloader;

import com.androidsx.rainnotifications.dailyclothes.model.Clothes;

import java.util.List;

public interface ClothesLoaderListener {
    public void onClothesLoaderSuccess(List<Clothes> clothesList);

    public void onClothesLoaderFailure(ClothesLoaderException exception);
}
