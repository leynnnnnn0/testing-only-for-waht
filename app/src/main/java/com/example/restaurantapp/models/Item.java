package com.example.restaurantapp.models;

public class Item {
    public static final String TABLE_NAME = "items";

    public int id, reviewCount, isAvailable, isFavorite;
    public double price, rating;
    public String image, name, category, detail, tag, location, description;
}
