package com.example.restaurantapp.models;

public class User {
    public static final String TABLE_NAME = "users";

    public int id, age, creditPoints, isActive;
    public String image, fullName, username, password, phone, gender, role, specialty;
    public String email, location, joinedDate;
}
