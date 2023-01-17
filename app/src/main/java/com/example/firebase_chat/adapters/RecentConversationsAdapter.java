package com.example.firebase_chat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase_chat.databinding.ItemContainerRecentConversationBinding;
import com.example.firebase_chat.utilities.Message;

import java.util.List;

public class RecentConversationsAdapter extends  RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    private final List<Message> messages;
    String mainUserName;

    public RecentConversationsAdapter(List<Message> messages, String mainUserName) {
        this.messages = messages;
        this.mainUserName = mainUserName;
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

        public String getOtherUserName(String name1, String name2) {
            if (name1.equals(mainUserName)) return name2;
            if (name2.equals(mainUserName)) return name1;
            return null;
        }

    }
}
