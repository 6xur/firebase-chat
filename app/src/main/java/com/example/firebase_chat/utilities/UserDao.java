package com.example.firebase_chat.utilities;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * The set method doesn't exist because Firebase loads and synchronises data asynchronously.
 * To retrieve a Java object, we attach a listener to our database reference.
 */
public class UserDao implements Dao<User> {

    private final DatabaseReference databaseRef;
    String databaseUrl = "https://fir-chat-d5a58-default-rtdb.asia-southeast1.firebasedatabase.app";

    public UserDao() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseRef = database.getReference("Users");
    }

    @Override
    public Task<Void> add(User user) {
        return databaseRef.child(Objects.requireNonNull(FirebaseAuth.getInstance()
                .getCurrentUser()).getUid()).setValue(user);
    }

    @Override
    public Task<Void> delete() {
        return databaseRef.child(Objects.requireNonNull(FirebaseAuth.getInstance()
                .getCurrentUser()).getUid()).removeValue();
    }

    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }
}
