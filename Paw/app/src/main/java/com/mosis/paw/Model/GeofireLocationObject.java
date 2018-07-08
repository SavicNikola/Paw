package com.mosis.paw.Model;

import com.firebase.geofire.GeoLocation;

import java.util.ArrayList;

public class GeofireLocationObject {

    private String key;
    private String g;   //kljuc
    private ArrayList<Double> l;

    public GeofireLocationObject() {

    }

    public GeofireLocationObject(String key, String g, ArrayList<Double> l) {
        this.key = key;
        this.g = g;
        this.l = l;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public ArrayList<Double> getL() {
        return l;
    }

    public void setL(ArrayList<Double> l) {
        this.l = l;
    }

    public GeoLocation getLocation() {
        return new GeoLocation(l.get(0), l.get(1));
    }
}
