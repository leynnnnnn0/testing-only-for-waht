package com.example.restaurantapp.models;

public class FindChatMessage {
    public static final int PET = 0, USER = 1, MATCHES = 2;
    public int type;
    public String text;

    public FindChatMessage(int type, String text) {
        this.type = type;
        this.text = text;
    }
}
