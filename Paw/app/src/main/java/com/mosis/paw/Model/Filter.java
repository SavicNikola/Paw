package com.mosis.paw.Model;

public class Filter {
    private String type;
    private String color;
    private String size;

    public Filter(String type, String color, String size) {
        this.type = type;
        this.color = color;
        this.size = size;
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
}
