package com.example.firebase_chat.utilities;

import java.io.Serializable;

public class User implements Serializable {

    public String name, email, Uid;
    public String bio;
    public String phone, location;
    public String imgUri;

    public User() {

    }

    public User(String name, String email, String Uid) {
        this.name = name;
        this.email = email;
        this.Uid = Uid;
    }
}
