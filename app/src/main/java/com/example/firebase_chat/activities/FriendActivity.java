package com.example.firebase_chat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firebase_chat.R;
import com.example.firebase_chat.utilities.User;
import com.example.firebase_chat.utilities.UserDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FriendActivity extends AppCompatActivity {

    Uri imageUri;
    ImageView profilePicture;

    TextView name;
    TextView email;

    UserDao userDao;
    User retrievedUser;
    String emailStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            emailStr = bundle.getString("email");
        }

        userDao = new UserDao();
        userDao.getDatabaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot user: snapshot.getChildren()) {
                    if(user.child("email").getValue().equals(emailStr)) {
                        retrievedUser = user.getValue(User.class);
                        if(retrievedUser != null) {
                            name.setText(retrievedUser.name);
                            email.setText(retrievedUser.email);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}