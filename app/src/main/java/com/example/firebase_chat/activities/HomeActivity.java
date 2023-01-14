package com.example.firebase_chat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.firebase_chat.fragments.ChatFragment;
import com.example.firebase_chat.fragments.UsersFragment;
import com.example.firebase_chat.fragments.ProfileFragment;
import com.example.firebase_chat.R;
import com.example.firebase_chat.databinding.ActivityHomeBinding;
import com.example.firebase_chat.utilities.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    BottomNavigationView bottomNavigationView;

    public static final int CHAT_FRAGMENT = 0;
    public static final int USERS_FRAGMENT = 1;
    public static final int PROFILE_FRAGMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_home);
        replaceFragment(CHAT_FRAGMENT);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.chat:
                    replaceFragment(CHAT_FRAGMENT);
                    break;
                case R.id.users:
                    replaceFragment(USERS_FRAGMENT);
                    break;
                case R.id.profile:
                    replaceFragment(PROFILE_FRAGMENT);
                    break;
            }
            return true;
        });
    }

    /**
     * Switch whatever is in the frame layout with the new fragment
     * 0 - Chat fragment
     * 1 - Users fragment
     * 2 - Profile fragment
     **/
    private void replaceFragment(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            case USERS_FRAGMENT:
                if (fragmentManager.findFragmentByTag(Constants.KEY_USERS) != null) {
                    // If the users fragment exists, show it
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(Constants.KEY_USERS)).commit();
                } else {
                    // If the users fragment doesn't exist, add it to fragment manager
                    fragmentManager.beginTransaction().add(R.id.frameLayout, new UsersFragment(), Constants.KEY_USERS).commit();
                }
                if (fragmentManager.findFragmentByTag(Constants.KEY_PROFILE) != null) {
                    // If the other fragment is visible, hide it
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(Constants.KEY_PROFILE)).commit();
                }
                if (fragmentManager.findFragmentByTag("chat") != null) {
                    // If the other fragment is visible, hide it
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("chat")).commit();
                }
                break;
            case PROFILE_FRAGMENT:
                if (fragmentManager.findFragmentByTag(Constants.KEY_PROFILE) != null) {
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(Constants.KEY_PROFILE)).commit();
                } else {
                    fragmentManager.beginTransaction().add(R.id.frameLayout, new ProfileFragment(), Constants.KEY_PROFILE).commit();
                }
                if (fragmentManager.findFragmentByTag(Constants.KEY_USERS) != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(Constants.KEY_USERS)).commit();
                }
                if (fragmentManager.findFragmentByTag("chat") != null) {
                    // If the other fragment is visible, hide it
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("chat")).commit();
                }
                break;
            case CHAT_FRAGMENT:
                if (fragmentManager.findFragmentByTag("chat") != null) {
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("chat")).commit();
                } else {
                    fragmentManager.beginTransaction().add(R.id.frameLayout, new ChatFragment(), "chat").commit();
                }
                if (fragmentManager.findFragmentByTag(Constants.KEY_PROFILE) != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(Constants.KEY_PROFILE)).commit();
                }
                if (fragmentManager.findFragmentByTag(Constants.KEY_USERS) != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(Constants.KEY_USERS)).commit();
                }
        }
    }
}