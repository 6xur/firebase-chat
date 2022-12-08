package com.example.firebase_chat;

public class User {

    public String name, email, Uid;
    public String bio;
    public String phone, location;

    public User() {

    }

    public User(String name, String email, String Uid) {
        this.name = name;
        this.email = email;
        this.Uid = Uid;
    }
}
