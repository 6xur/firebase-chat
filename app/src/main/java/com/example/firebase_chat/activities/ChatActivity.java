package com.example.firebase_chat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_chat.R;
import com.example.firebase_chat.adapters.ChatAdapter;
import com.example.firebase_chat.utilities.Constants;
import com.example.firebase_chat.utilities.Message;
import com.example.firebase_chat.utilities.MessageDao;
import com.example.firebase_chat.utilities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private AppCompatImageView imageBack;
    private TextView name, inputMessage;
    private RecyclerView chatRecyclerView;
    private FrameLayout layoutSend;

    private User mainUser;
    private User friend;  // user we're chatting with (note that the app does not support friending)
    private List<Message> messages;
    private ChatAdapter chatAdapter;

    private MessageDao messageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        name = findViewById(R.id.textName);
        imageBack = findViewById(R.id.imageBack);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        inputMessage = findViewById(R.id.inputMessage);
        layoutSend = findViewById(R.id.layoutSend);

        mainUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        friend = (User) getIntent().getSerializableExtra(Constants.KEY_FRIEND);
        name.setText(friend.name);

        setListeners();
        init();
    }

    private void init() {
        //preferenceManager = new PreferenceManager(getApplicationContext());
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                messages,
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );
        chatRecyclerView.setAdapter(chatAdapter);
        messageDao = new MessageDao();

        messageDao.getDatabaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot chat : snapshot.getChildren()) {  // a conversation is between two users
                    if(chat.getKey().equals(messageDao.getMessagePath(mainUser.Uid, friend.Uid))) {  // existing conversation
                        System.out.println(mainUser.name + " and " + friend.name + " have talked before");
                        // TODO: Create message objects and add them to messages
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {
        if(inputMessage.getText().length() > 0) {
            Message message = new Message(mainUser, friend, inputMessage.getText().toString());
            messageDao.add(message);
            inputMessage.setText(null);
        }
    }

    private void setListeners() {
        imageBack.setOnClickListener(v -> onBackPressed());
        layoutSend.setOnClickListener(v -> sendMessage());
    }

}