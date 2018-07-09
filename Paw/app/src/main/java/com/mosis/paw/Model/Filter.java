package com.mosis.paw.Model;

public class Filter {
    private String type;
    private String color;
    private String size;

    private String radius;

    public Filter(String type, String color, String size, String radius) {
        this.type = type;
        this.color = color;
        this.size = size;

        this.radius = radius;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }
}
