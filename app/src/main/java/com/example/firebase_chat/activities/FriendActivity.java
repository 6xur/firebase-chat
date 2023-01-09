package com.example.firebase_chat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firebase_chat.R;
import com.example.firebase_chat.utilities.User;
import com.example.firebase_chat.utilities.UserDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FriendActivity extends AppCompatActivity {

    ImageView profilePicture;
    TextView name;
    TextView bio;
    TextView phone, email, location;
    LinearLayout phoneContainer, locationContainer;

    UserDao userDao;
    User retrievedUser;
    String emailStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        profilePicture = findViewById(R.id.profilePicture);
        name = findViewById(R.id.name);
        bio = findViewById(R.id.bio);
        phone = findViewById(R.id.phone);
        phoneContainer = findViewById(R.id.phoneContainer);
        email = findViewById(R.id.email);
        location = findViewById(R.id.location);
        locationContainer = findViewById(R.id.locationContainer);

        // Initially invisible, only display these if they are stored in Firebase
        phoneContainer.setVisibility(View.GONE);
        locationContainer.setVisibility(View.GONE);

        // Get the email of the selected user
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            emailStr = bundle.getString("email");
        }

        userDao = new UserDao();
        userDao.getDatabaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (user.child("email").getValue().equals(emailStr)) {
                        retrievedUser = user.getValue(User.class);
                        if (retrievedUser != null) {
                            name.setText(retrievedUser.name);
                            email.setText(retrievedUser.email);

                            if (retrievedUser.bio != null) {
                                bio.setText(retrievedUser.bio);
                            }

                            if (retrievedUser.phone != null) {
                                phone.setText(retrievedUser.phone);
                                phoneContainer.setVisibility(View.VISIBLE);
                            } else {
                                phoneContainer.setVisibility(View.GONE);
                            }

                            if (retrievedUser.location != null) {
                                location.setText(retrievedUser.location);
                                locationContainer.setVisibility(View.VISIBLE);
                            } else {
                                locationContainer.setVisibility(View.GONE);
                            }

                            if (retrievedUser.imgUri != null) {
                                Picasso.get().load(retrievedUser.imgUri).into(profilePicture);
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}