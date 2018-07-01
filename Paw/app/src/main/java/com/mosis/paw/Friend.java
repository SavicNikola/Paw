package com.mosis.paw;

public class Friend {

    private String name;
    private Integer image;
    private String place;

    public Friend(String name, Integer image, String place) {
        this.name = name;
        this.image = image;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public Integer getImage() {
        return image;
    }

    public String getPlace() {
        return place;
    }
}
