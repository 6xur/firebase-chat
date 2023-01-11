package com.example.firebase_chat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.firebase_chat.R;
import com.example.firebase_chat.utilities.Constants;
import com.example.firebase_chat.utilities.User;

public class ChatActivity extends AppCompatActivity {

    AppCompatImageView imageBack;
    TextView name;

    User friend;  // user we're chatting with (note that the app does not support friending)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        name = findViewById(R.id.textName);
        imageBack = findViewById(R.id.imageBack);

        friend = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        name.setText(friend.name);

        setListeners();
    }

    private void setListeners() {
        imageBack.setOnClickListener(v -> onBackPressed());
    }

}