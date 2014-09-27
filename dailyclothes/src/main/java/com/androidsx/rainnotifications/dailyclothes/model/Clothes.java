package com.androidsx.rainnotifications.dailyclothes.model;

public class Clothes {

    private String magazine;
    private String subtitle;
    private String description;
    private int logo;
    private int photo;

    public Clothes (String magazine, String subtitle, String description, int logo, int photo) {
        this.magazine = magazine;
        this.subtitle = subtitle;
        this.description = description;
        this.logo = logo;
        this.photo = photo;
    }

    public String getMagazine() {
        return magazine;
    }

    public String getSubtitle() {
        return subtitle;
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
