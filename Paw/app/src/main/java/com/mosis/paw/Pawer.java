package com.mosis.paw;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Pawer extends User {

    private static final Pawer instance = new Pawer();
    //private static final Pawer instance = null;

    private Pawer() {
        //favourites;

//        // TODO: proba podaci obrisati posle
//        setEmail("gogidotcom");
//
//        // povucemo svoji favourites
//        FirebaseSingleton.getInstance().databaseReference
//                .child("users")
//                .child(getEmail())
//                .child("favourites")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        setFavourites((ArrayList<String>) dataSnapshot.getValue());
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
    }

    public static Pawer getInstance() {

        return instance;
    }

    private Integer avatar;

    private String points;
    private String helps;
    private String friends;

    private String latitude;
    private String longitude;

    private ArrayList<String> favourites;

    public Pawer(User user) {
        key = user.getKey();
        name = user.getName();
        email = user.getEmail();
        password = user.getPassword();
        phone = user.getPhone();
        city = user.getCity();

        this.points = "0";
        this.helps = "0";
        this.friends = "0";
        this.avatar = new Random().nextInt(3)+1;    //generise od 0 do 3-1, mora se doda 1 //todo:promeni kasnije kad se doda jos avatara
        favourites = new ArrayList<>();
    }

    public ArrayList<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(ArrayList<String> favourites) {
        this.favourites = favourites;
    }

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
}
