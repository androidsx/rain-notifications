package com.androidsx.rainnotifications.dailyclothes.model.clothesloader.local;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.squareup.picasso.Picasso;

public class LocalClothes extends Clothes{

    public static final Parcelable.Creator<LocalClothes> CREATOR = new Parcelable.Creator<LocalClothes>() {
        public LocalClothes createFromParcel(Parcel in) {
            return new LocalClothes(in);
        }

        public LocalClothes[] newArray(int size) {
            return new LocalClothes[size];
        }
    };

    private final String imagePath;

    public LocalClothes(String imagePath) {
        this.imagePath = imagePath;
    }

    private LocalClothes(Parcel in) {
        imagePath = in.readString();
    }

    @Override
    public void loadOnImageView(Context context, ImageView imageView) {
        Picasso.with(context).load(imagePath).into(imageView);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(imagePath);
    }
}
