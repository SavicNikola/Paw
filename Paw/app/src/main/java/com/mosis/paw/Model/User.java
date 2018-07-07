package com.mosis.paw.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Parcelable{

    @Exclude
    protected String key;

    protected String name;
    protected String email;
    protected String password;
    protected String phone;
    protected String city;
    protected String imageUrl;

    public User() {

    }

    public User(String key, String name, String email, String password, String phone, String city) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.city = city;
    }

    public User(String key, String name, String email, String password, String phone, String city, String imageUrl) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.city = city;
        this.imageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    protected User(Parcel in) {
        key = in.readString();
        name = in.readString();
        email = in.readString();
        password = in.readString();
        phone = in.readString();
        city = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(phone);
        dest.writeString(city);
        dest.writeString(imageUrl);
    }
}
