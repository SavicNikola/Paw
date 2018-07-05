package com.mosis.paw.Model;

public class Post {

    private String id;
    private String userId;
    private String time;
    private String images;
    private String city;
    private String type;
    private String description;

    private String animalColor;
    private String animalSize;
    private String animalType;

    private String mapLatitude;
    private String mapLongitude;

    public Post() {
    }

    public Post(String id, String userId, String time, String images, String city,String type, String desc, String animalColor, String animalSize, String animalType, String mapLatitude, String mapLongitude) {
        this.id = id;
        this.time = time;
        this.images = images;
        this.city = city;
        this.type = type;
        this.description = desc;
        this.animalColor = animalColor;
        this.animalSize = animalSize;
        this.animalType = animalType;
        this.mapLatitude = mapLatitude;
        this.mapLongitude = mapLongitude;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getImages() {
        return images;
    }

    public String getCity() {
        return city;
    }

    public String getType() {
        return type;
    }

    public String getAnimalColor() {
        return animalColor;
    }

    public String getAnimalSize() {
        return animalSize;
    }

    public String getAnimalType() {
        return animalType;
    }

    public String getMapLatitude() {
        return mapLatitude;
    }

    public String getMapLongitude() {
        return mapLongitude;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAnimalColor(String animalColor) {
        this.animalColor = animalColor;
    }

    public void setAnimalSize(String animalSize) {
        this.animalSize = animalSize;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public void setMapLatitude(String mapLatitude) {
        this.mapLatitude = mapLatitude;
    }

    public void setMapLongitude(String mapLongitude) {
        this.mapLongitude = mapLongitude;
    }
}

