package com.androidsx.rainnotifications.dailyclothes.model;

import android.content.Context;
import android.os.Parcelable;
import android.widget.ImageView;

public abstract class Clothes implements Parcelable{
    public abstract void loadOnImageView(Context context, ImageView imageView);
}
