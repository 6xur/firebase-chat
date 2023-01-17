package com.example.firebase_chat.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public String message;
    public String senderUid, receiverUid;
    public String senderName, receiverName;
    public String timestamp;

    public Message() {}

    public Message(String senderUid, String senderName, String receiverUid, String receiverName, String message) {
        this.senderUid = senderUid;
        this.senderName = senderName;
        this.receiverUid = receiverUid;
        this.receiverName = receiverName;
        this.message = message;
        this.timestamp = getReadableTimeStamp(new Date());  // timestamp is the current time
    }

    public Message(String senderUid, String senderName, String receiverUid, String receiverName, String message, String timestamp) {
        this.senderUid = senderUid;
        this.senderName = senderName;
        this.receiverUid = receiverUid;
        this.receiverName = receiverName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getReadableTimeStamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        return sdf.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Message)) {
            return false;
        }

        Message message = (Message) o;
        return message.message.equals(this.message)
                && message.senderUid.equals(this.senderUid)
                && message.senderName.equals(this.senderName)
                && message.receiverUid.equals(this.receiverUid)
                && message.receiverName.equals(this.receiverName)
                && message.timestamp.equals(this.timestamp);
    }
}
