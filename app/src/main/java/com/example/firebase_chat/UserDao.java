package com.example.firebase_chat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * The set method doesn't exist because Firebase loads and synchronises data asynchronously.
 * To retrieve a Java object, we attach a listener to our database reference.
 */
public class UserDao implements Dao<User> {

    private final DatabaseReference databaseReference;

    public UserDao() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fir-chat-d5a58-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = database.getReference("Users");
    }

    @Override
    public Task<Void> add(User user) {
        return databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
    }

    @Override
    public Task<Void> delete(User user) {
        return null;
    }

    public DatabaseReference getDatabaseReference(){
        return databaseReference;
    }
}
