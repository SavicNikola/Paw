package com.mosis.paw;

public class Friend {

    private String name;
    private String image;
    private String place;

    public Friend(String name, String image, String place) {
        this.name = name;
        this.image = image;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getPlace() {
        return place;
    }
}
