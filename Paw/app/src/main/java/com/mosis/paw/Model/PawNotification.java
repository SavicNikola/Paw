package com.mosis.paw.Model;

public class PawNotification {

    private String id;
    private String latitude;
    private String longitude;
    private String user;
    private String picture;
    private String description;
    private String type;
    private boolean read;


    public static final String FOUND = "found";
    public static final String ADOPT = "adopt";

    public PawNotification () {

    }

    public PawNotification(String id, String latitude, String longitude, String user, String picture, String description, String type) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.picture = picture;
        this.description = description;
        this.type = type;
        read = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
