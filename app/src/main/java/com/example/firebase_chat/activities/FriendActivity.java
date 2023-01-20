package com.example.firebase_chat.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebase_chat.R;
import com.example.firebase_chat.utilities.Constants;
import com.example.firebase_chat.utilities.User;
import com.example.firebase_chat.utilities.UserDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FriendActivity extends AppCompatActivity {

    ImageView profilePicture;
    TextView name, bio, phone, email, location;
    LinearLayout phoneContainer, locationContainer;
    Button chatBtn;

    ClipboardManager clipboard;
    UserDao userDao;
    User mainUser, friend;
    String friendUid;

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

        setListeners();

        // Initially invisible, only display these if they are stored in Firebase
        phoneContainer.setVisibility(View.GONE);
        locationContainer.setVisibility(View.GONE);

        // Get the Uid of the selected user
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            friendUid = bundle.getString(Constants.KEY_UID);
        }

        userDao = new UserDao();
        userDao.getDatabaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    // Retrieve main user
                    if (user.getKey() != null
                            && FirebaseAuth.getInstance().getCurrentUser() != null
                            && user.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        mainUser = user.getValue(User.class);
                    }
                    // Retrieve friend
                    if (Objects.equals(user.child(Constants.KEY_UID).getValue(), friendUid)) {
                        friend = user.getValue(User.class);
                        if (friend != null) {
                            name.setText(friend.name);
                            email.setText(friend.email);

                            if (friend.bio != null) {
                                bio.setText(friend.bio);
                            }

                            if (friend.phone != null) {
                                phone.setText(friend.phone);
                                phoneContainer.setVisibility(View.VISIBLE);
                            } else {
                                phoneContainer.setVisibility(View.GONE);
                            }

                            if (friend.location != null) {
                                location.setText(friend.location);
                                locationContainer.setVisibility(View.VISIBLE);
                            } else {
                                locationContainer.setVisibility(View.GONE);
                            }

                            if (friend.imgUri != null) {
                                Picasso.get().load(friend.imgUri).into(profilePicture);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setListeners() {
        phone.setOnClickListener(view -> clipboard.setPrimaryClip(ClipData.newPlainText("Phone", phone.getText())));
        email.setOnClickListener(view -> clipboard.setPrimaryClip(ClipData.newPlainText("Email", email.getText())));
        location.setOnClickListener(view -> clipboard.setPrimaryClip(ClipData.newPlainText("Location", location.getText())));
        chatBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra(Constants.KEY_USER, mainUser);
            intent.putExtra(Constants.KEY_FRIEND, friend);
            startActivity(intent);
        });
    }
}