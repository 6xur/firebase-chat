package com.example.firebase_chat.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.firebase_chat.R;
import com.example.firebase_chat.adapters.UsersAdapter;
import com.example.firebase_chat.utilities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersFragment extends Fragment {

    private ArrayList<User> users;
    private ArrayList<User> usersCopy;
    private UsersAdapter usersAdapter;
    private RecyclerView recyclerView;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        setHasOptionsMenu(true);

        // Set users adapter
        users = new ArrayList<>();
        usersCopy = new ArrayList<>();
        usersAdapter = new UsersAdapter(users);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(usersAdapter);

        // Showing Firebase data in the recycler view
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fir-chat-d5a58-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference usersReference = database.getReference("Users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    System.out.println(ds.getKey());
                    String name = ds.child("name").getValue(String.class);
                    String email = ds.child("email").getValue(String.class);
                    String Uid = ds.child("Uid").getValue(String.class);
                    String imgUri = ds.child("imgUri").getValue(String.class);
                    User user = new User(name, email, Uid);
                    if (imgUri != null) {
                        user.imgUri = imgUri;
                    }
                    users.add(user);
                    usersCopy.add(user);
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getMessage());
            }
        };
        usersReference.addListenerForSingleValueEvent(eventListener);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.user_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search for users");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filter(s);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // TODO: usersadapter filter
                filter(s);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void filter(String text) {
        users.clear();
        if (text.isEmpty()) {
            users.addAll(usersCopy);
        } else {
            text = text.toLowerCase();
            for (User user : usersCopy) {
                if (user.name.toLowerCase().contains(text) || user.email.toLowerCase().contains(text)) {
                    users.add(user);
                }
            }
        }
        usersAdapter.notifyDataSetChanged();
    }

}