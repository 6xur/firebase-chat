package com.example.firebase_chat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_chat.R;
import com.example.firebase_chat.utilities.User;
import com.example.firebase_chat.utilities.UserDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FriendActivity extends AppCompatActivity implements View.OnClickListener {

    ClipboardManager clipboard;

    ImageView profilePicture;
    TextView name;
    TextView bio;
    TextView phone, email, location;
    LinearLayout phoneContainer, locationContainer;
    Button chatBtn;

    UserDao userDao;
    User retrievedUser;
    String emailStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        profilePicture = findViewById(R.id.profilePicture);
        name = findViewById(R.id.name);
        bio = findViewById(R.id.bio);
        phone = findViewById(R.id.phone);
        phoneContainer = findViewById(R.id.phoneContainer);
        email = findViewById(R.id.email);
        location = findViewById(R.id.location);
        locationContainer = findViewById(R.id.locationContainer);
        chatBtn = findViewById(R.id.chatBtn);

        phone.setOnClickListener(this);
        email.setOnClickListener(this);
        location.setOnClickListener(this);
        chatBtn.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        ClipData clip;
        switch (view.getId()) {
            case R.id.phone:
                clip = ClipData.newPlainText("Phone", phone.getText());
                clipboard.setPrimaryClip(clip);
                break;
            case R.id.email:
                clip = ClipData.newPlainText("Email", email.getText());
                clipboard.setPrimaryClip(clip);
                break;
            case R.id.location:
                clip = ClipData.newPlainText("Location", location.getText());
                clipboard.setPrimaryClip(clip);
                break;
            case R.id.chatBtn:
                Toast.makeText(getApplicationContext(), "Chat clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}