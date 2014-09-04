package com.androidsx.rainnotifications.util;

import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class AnimationHelper {

    public static void applyCardAnimation(View cardView) {
        YoYo.with(Techniques.BounceInUp)
                .duration(800)
                .playOn(cardView);
    }

    public static void applyMascotAnimation(View mascotView) {
        YoYo.with(Techniques.Pulse)
                .duration(300)
                .playOn(mascotView);
    }
}
