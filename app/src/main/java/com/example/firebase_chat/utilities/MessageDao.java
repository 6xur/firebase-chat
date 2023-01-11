package com.example.firebase_chat.utilities;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageDao implements Dao<Message> {

    private final DatabaseReference databaseRef;
    String databaseUrl = "https://fir-chat-d5a58-default-rtdb.asia-southeast1.firebasedatabase.app";

    public MessageDao() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseRef = database.getReference("Messages");
    }

    @Override
    public Task<Void> add(Message message) {
        String senderUid = message.senderUid;
        String receiverUid = message.receiverUid;
        String path = getMessagePath(senderUid, receiverUid);
        return databaseRef.child(path).child(message.timestamp).setValue(message);
    }

    public String getMessagePath(String senderUid, String receiverUid) {
        int compare = senderUid.compareTo(receiverUid);
        if (compare < 0) {
            return senderUid + "_" + receiverUid;
        } else {
            return receiverUid + "_" + senderUid;
        }
    }

    @Override
    public Task<Void> delete() {
        return null;
    }

    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }

}
