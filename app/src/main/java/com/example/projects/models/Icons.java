package com.example.projects.models;

public class Icons {
    private int image;

    public Icons(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Icons{" +
                "image=" + image +
                '}';
    }
}

