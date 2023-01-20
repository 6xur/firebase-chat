package com.example.firebase_chat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase_chat.R;
import com.example.firebase_chat.activities.ChatActivity;
import com.example.firebase_chat.adapters.RecentConversationsAdapter;
import com.example.firebase_chat.utilities.Constants;
import com.example.firebase_chat.utilities.Message;
import com.example.firebase_chat.utilities.MessageDao;
import com.example.firebase_chat.utilities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentConversationFragment extends Fragment {

    private List<Message> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private RecyclerView conversationsRecyclerView;
    private FirebaseUser firebaseUser;

    public RecentConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_conversation, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set recent conversation adapter
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, message -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            User mainUser = new User();
            mainUser.Uid = firebaseUser.getUid();
            mainUser.name = firebaseUser.getDisplayName();
            User friend = new User();
            friend.Uid = conversationsAdapter.getOtherUserUid(message.senderUid, message.receiverUid);
            friend.name = conversationsAdapter.getOtherUserName(message.senderName, message.receiverName);
            intent.putExtra(Constants.KEY_USER, mainUser);
            intent.putExtra(Constants.KEY_FRIEND, friend);
            startActivity(intent);
        });
        conversationsRecyclerView = view.findViewById(R.id.conversationRecyclerView);
        conversationsRecyclerView.setAdapter(conversationsAdapter);

        // Show Recent conversations in Recyclerview
        MessageDao messageDao = new MessageDao();
        messageDao.getDatabaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversations.clear();
                for (DataSnapshot conversation : snapshot.getChildren()) {
                    if (conversation.getKey() != null && !conversation.getKey().contains(firebaseUser.getUid())) {
                        continue;
                    }
                    Message messageObj = new Message();
                    for (DataSnapshot message : conversation.getChildren()) {
                        messageObj.message = message.child("message").getValue(String.class);
                        messageObj.receiverName = message.child("receiverName").getValue(String.class);
                        messageObj.receiverUid = message.child("receiverUid").getValue(String.class);
                        messageObj.senderName = message.child("senderName").getValue(String.class);
                        messageObj.senderUid = message.child("senderUid").getValue(String.class);
                        messageObj.timestamp = message.child("timestamp").getValue(String.class);
                    }
                    conversations.add(messageObj);
                }

                // Sort messages by timestamp
                conversations.sort(new Comparator<Message>() {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

                    @Override
                    public int compare(Message m1, Message m2) {
                        try {
                            return sdf.parse(m1.timestamp).compareTo(sdf.parse(m2.timestamp));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

                // Reverse the list because we want the newest messages first (later dates first)
                Collections.reverse(conversations);

                conversationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}