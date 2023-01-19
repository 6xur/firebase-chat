package com.example.firebase_chat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase_chat.databinding.ItemContainerRecentConversationBinding;
import com.example.firebase_chat.utilities.Message;

import java.util.List;

public class RecentConversationsAdapter extends  RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    public interface OnConversationClickListener {
        void onItemClick(Message message);
    }

    private final List<Message> messages;
    String mainUserName;
    private final OnConversationClickListener listener;

    public RecentConversationsAdapter(List<Message> messages, String mainUserName, OnConversationClickListener listener) {
        this.messages = messages;
        this.mainUserName = mainUserName;
        this.listener = listener;
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
            // TODO: Load image
            binding.nameText.setText(getOtherUserName(message.senderName, message.receiverName));
            binding.recentMessageText.setText(message.message);
        }

        public void bind(final Message message, final RecentConversationsAdapter.OnConversationClickListener listener) {
            itemView.setOnClickListener(view -> listener.onItemClick(message));
        }
    }

    public String getOtherUserName(String name1, String name2) {
        if (name1.equals(mainUserName)) return name2;
        if (name2.equals(mainUserName)) return name1;
        return null;
    }
}
