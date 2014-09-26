package com.androidsx.rainnotifications.dailyclothes.model;

public class Clothes {

    private String magazine;
    private String mSubtitle;
    private String description;
    private int logo;
    private int photo;

    public Clothes (String magazine, String mSubtitle, String description, int logo, int photo) {
        this.magazine = magazine;
        this.mSubtitle = mSubtitle;
        this.description = description;
        this.logo = logo;
        this.photo = photo;
    }

    public String getMagazine() {
        return magazine;
    }

    public String getmSubtitle() {
        return mSubtitle;
    }

    public String getDescription() {
        return description;
    }

    public int getLogo() {
        return logo;
    }

    public int getPhoto() {
        return photo;
    }
}
