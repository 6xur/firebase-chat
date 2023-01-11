package com.example.firebase_chat.utilities;

import java.text.SimpleDateFormat;

public class Message {
    public String message;
    public String senderUid, receiverUid;
    public String senderName, receiverName;
    public String timestamp;

    public Message(User sender, User receiver, String message) {
        this.senderUid = sender.Uid;
        this.senderName = sender.name;
        this.receiverUid = receiver.Uid;
        this.receiverName = receiver.name;
        this.message = message;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date());
    }
}
