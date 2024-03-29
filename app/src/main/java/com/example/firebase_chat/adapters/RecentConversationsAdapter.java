package com.example.firebase_chat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase_chat.databinding.ItemContainerRecentConversationBinding;
import com.example.firebase_chat.utilities.Constants;
import com.example.firebase_chat.utilities.Message;
import com.example.firebase_chat.utilities.UserDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class RecentConversationsAdapter extends  RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    public interface OnConversationClickListener {
        void onItemClick(Message message);
    }

    private final List<Message> messages;
    private final OnConversationClickListener listener;
    private final FirebaseUser mainUser;

    public RecentConversationsAdapter(List<Message> messages, OnConversationClickListener listener) {
        this.messages = messages;
        this.listener = listener;
        this.mainUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(messages.get(position));
        holder.bind(messages.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversationBinding binding;

        ConversionViewHolder(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding) {
            super(itemContainerRecentConversationBinding.getRoot());
            binding = itemContainerRecentConversationBinding;
        }

        void setData(Message message) {
            binding.nameText.setText(getOtherUserName(message.senderName, message.receiverName));
            if (message.senderUid.equals(mainUser.getUid())) {
                String mainUserMessage = "You: " + message.message;
                binding.recentMessageText.setText(mainUserMessage);
            } else {
                binding.recentMessageText.setText(message.message);
            }
            // Load other user's profile image
            String Uid = getOtherUserUid(message.senderUid, message.receiverUid);
            UserDao userDao = new UserDao();
            userDao.getDatabaseRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (Objects.equals(ds.getKey(), Uid)) {
                            String imgUri = ds.child(Constants.KEY_IMG_URI).getValue(String.class);
                            if (imgUri != null) {
                                Picasso.get().load(imgUri).into(binding.profileImage);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void bind(final Message message, final RecentConversationsAdapter.OnConversationClickListener listener) {
            itemView.setOnClickListener(view -> listener.onItemClick(message));
        }
    }

    public String getOtherUserName(String name1, String name2) {
        String mainUserName = mainUser.getDisplayName();
        if (name1.equals(mainUserName)) return name2;
        if (name2.equals(mainUserName)) return name1;
        return null;
    }

    public String getOtherUserUid(String Uid1, String Uid2) {
        String mainUid = mainUser.getUid();
        if (Uid1.equals(mainUid)) return Uid2;
        if (Uid2.equals(mainUid)) return Uid1;
        return null;
    }
}
