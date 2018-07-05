package com.mosis.paw;

import com.mosis.paw.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Pawer extends User {

    private static final Pawer instance = new Pawer();

    private Pawer() {
        favourites = new ArrayList<String>();

        // TODO: proba podaci obrisati posle
        setEmail("peraperic");
    }

    public static Pawer getInstance() { return instance;}

    private Integer avatar;

    private String points;
    private String helps;
    private String friends;

    private String latitude;
    private String longitude;

    private List<String> favourites;

    public void initValues(Pawer values) {

    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getHelps() {
        return helps;
    }

    public void setHelps(String helps) {
        this.helps = helps;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
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

    public List getFavourites() {
        return favourites;
    }

    public void setFavourites(List favourites) {
        this.favourites = favourites;
    }
}
