package com.example.firebase_chat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase_chat.R;
import com.example.firebase_chat.utilities.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    private final ArrayList<User> users;
    private final OnItemClickListener listener;

    public UsersAdapter(ArrayList<User> users, OnItemClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView emailText;
        private final ImageView profileImage;

        public UsersViewHolder(final View view) {
            super(view);
            setIsRecyclable(false);  // do not recycle otherwise the images would be set wrong
            nameText = view.findViewById(R.id.nameText);
            emailText = view.findViewById(R.id.emailText);
            profileImage = view.findViewById(R.id.profileImage);
        }

        public void bind(final User user, final OnItemClickListener listener) {
            itemView.setOnClickListener(view -> listener.onItemClick(user));
        }
    }

    @NonNull
    @Override
    public UsersAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View usersView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user, parent, false);
        return new UsersViewHolder(usersView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UsersViewHolder holder, int position) {
        String name = users.get(position).name;
        String email = users.get(position).email;
        String imgUri = users.get(position).imgUri;
        holder.nameText.setText(name);
        holder.emailText.setText(email);
        if (imgUri != null) {
            Picasso.get().load(imgUri).into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.color.purple_200);
        }
        holder.bind(users.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}