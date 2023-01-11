package com.example.firebase_chat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.firebase_chat.fragments.UsersFragment;
import com.example.firebase_chat.fragments.ProfileFragment;
import com.example.firebase_chat.R;
import com.example.firebase_chat.databinding.ActivityHomeBinding;
import com.example.firebase_chat.utilities.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_home);
        replaceFragment(0);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.users:
                    replaceFragment(0);
                    break;
                case R.id.profile:
                    replaceFragment(1);
                    break;
            }
            return true;
        });
    }

    /**
     * Switch whatever is in the frame layout with the new fragment
     * 0 - Users Fragment
     * 1 - Profile Fragment
     **/
    private void replaceFragment(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            case 0:
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
                break;
            case 1:
                if (fragmentManager.findFragmentByTag(Constants.KEY_PROFILE) != null) {
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(Constants.KEY_PROFILE)).commit();
                } else {
                    fragmentManager.beginTransaction().add(R.id.frameLayout, new ProfileFragment(), Constants.KEY_PROFILE).commit();
                }
                if (fragmentManager.findFragmentByTag(Constants.KEY_USERS) != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(Constants.KEY_USERS)).commit();
                }
                break;
        }
    }
}