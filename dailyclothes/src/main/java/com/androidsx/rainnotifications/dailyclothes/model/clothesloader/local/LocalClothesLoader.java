package com.androidsx.rainnotifications.dailyclothes.model.clothesloader.local;

import android.os.Handler;

import com.androidsx.rainnotifications.dailyclothes.R;
import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.ClothesLoader;
import com.androidsx.rainnotifications.dailyclothes.model.clothesloader.ClothesLoaderListener;
import com.androidsx.rainnotifications.model.Day;

import java.util.ArrayList;
import java.util.List;

public class LocalClothesLoader implements ClothesLoader {

    private static final int SIMULATE_ASYNC_TASK_DELAY = 200;

    @Override
    public void loadClothes(Day day, final ClothesLoaderListener listener) {

        final List<Clothes> clothesList = new ArrayList<Clothes>();

        clothesList.add(new LocalClothes(R.drawable.lucky_3));
        clothesList.add(new LocalClothes(R.drawable.lucky_1));
        clothesList.add(new LocalClothes(R.drawable.lucky_2));
        clothesList.add(new LocalClothes(R.drawable.lucky_4));
        clothesList.add(new LocalClothes(R.drawable.lucky_5));
        /*
        clothesList.add(new LocalClothes(R.drawable.ann_taylor_1));
        clothesList.add(new LocalClothes(R.drawable.ann_taylor_2));
        clothesList.add(new LocalClothes(R.drawable.ann_taylor_3));
        clothesList.add(new LocalClothes(R.drawable.ann_taylor_4));
        clothesList.add(new LocalClothes(R.drawable.ann_taylor_5));
        clothesList.add(new LocalClothes(R.drawable.blogger_1));
        clothesList.add(new LocalClothes(R.drawable.blogger_2));
        clothesList.add(new LocalClothes(R.drawable.blogger_3));
        clothesList.add(new LocalClothes(R.drawable.blogger_4));
        clothesList.add(new LocalClothes(R.drawable.blogger_5));
        clothesList.add(new LocalClothes(R.drawable.blogger_6));
        clothesList.add(new LocalClothes(R.drawable.blogger_7));
        clothesList.add(new LocalClothes(R.drawable.blogger_8));
        clothesList.add(new LocalClothes(R.drawable.blogger_9));
        clothesList.add(new LocalClothes(R.drawable.blogger_10));
        */

        // This is to simulate an asynchronous task
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                listener.onClothesLoaderSuccess(clothesList);
            }
        }, SIMULATE_ASYNC_TASK_DELAY);
    }
}
