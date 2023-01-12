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

    public Message(String senderUid, String senderName, String receiverUid, String receiverName, String message, String timestamp) {
        this.senderUid = senderUid;
        this.senderName = senderName;
        this.receiverUid = receiverUid;
        this.receiverName = receiverName;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Message)) {
            return false;
        }

        // Typecast o to message so that we can compare data members
        Message message = (Message) o;

        return message.message.equals(this.message)
                && message.senderUid.equals(this.senderUid)
                && message.senderName.equals(this.senderName)
                && message.receiverUid.equals(this.receiverUid)
                && message.receiverName.equals(this.receiverName)
                && message.timestamp.equals(this.timestamp);
    }
}
