package com.mosis.paw;

public class Friend {

    private String name;
    private Integer avatar;
    private String city;
    private String email;

    public Friend() {}

    public Friend(String name, Integer avatar, String city, String email) {
        this.name = name;
        this.avatar = avatar;
        this.city = city;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
