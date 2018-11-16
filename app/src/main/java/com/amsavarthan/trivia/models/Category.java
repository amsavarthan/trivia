package com.amsavarthan.trivia.models;

public class Category {

    private String text,color,image;
    private int id;

    public Category(String text, String color, String image, int id) {
        this.text = text;
        this.color = color;
        this.image = image;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
