package com.example.firebase_chat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.firebase_chat.R;
import com.example.firebase_chat.adapters.ChatAdapter;
import com.example.firebase_chat.utilities.Constants;
import com.example.firebase_chat.utilities.Message;
import com.example.firebase_chat.utilities.MessageDao;
import com.example.firebase_chat.utilities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private AppCompatImageView imageBack;
    private TextView name, inputMessage;
    private RecyclerView chatRecyclerView;
    private FrameLayout layoutSend;

    private User mainUser;
    private User friend;  // user we are chatting with, note that the app doesn't support friending
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

        setListeners();

        mainUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        friend = (User) getIntent().getSerializableExtra(Constants.KEY_FRIEND);
        name.setText(friend.name);

        // Set chat adapter
        messages = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            chatAdapter = new ChatAdapter(messages, FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        chatRecyclerView.setAdapter(chatAdapter);

        // Display chat in Recyclerview
        messageDao = new MessageDao();
        messageDao.getDatabaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot chat : snapshot.getChildren()) {
                    if (chat.getKey() != null && chat.getKey().equals(messageDao.getMessagePath(mainUser.Uid, friend.Uid))) {
                        // Load all messages from the existing chat
                        for (DataSnapshot message : chat.getChildren()) {
                            String text = message.child("message").getValue(String.class);
                            String senderUid = message.child("senderUid").getValue(String.class);
                            String senderName = message.child("senderName").getValue(String.class);
                            String receiverUid = message.child("receiverUid").getValue(String.class);
                            String receiverName = message.child("receiverName").getValue(String.class);
                            String timestamp = message.child("timestamp").getValue(String.class);
                            Message messageObj = new Message(senderUid, senderName, receiverUid, receiverName, text, timestamp);
                            if (!messages.contains(messageObj)) {
                                messages.add(messageObj);
                            }
                        }
                    }
                }
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.smoothScrollToPosition(messages.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendMessage() {
        if (inputMessage.getText().length() > 0) {
            Message message = new Message(mainUser.Uid, mainUser.name, friend.Uid, friend.name, inputMessage.getText().toString());
            messageDao.add(message);
            inputMessage.setText(null);
        }
    }

    private void setListeners() {
        imageBack.setOnClickListener(view -> onBackPressed());
        layoutSend.setOnClickListener(view -> sendMessage());
    }
}