package com.example.firebase_chat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_chat.utilities.Dao;
import com.example.firebase_chat.R;
import com.example.firebase_chat.utilities.User;
import com.example.firebase_chat.utilities.UserDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

public class RegisterUserActivity extends AppCompatActivity {

    private TextView banner;
    private EditText editTextName, editTextEmail, editTextPassword;
    private Button registerUser;

    private FirebaseAuth mAuth;
    private static Dao<User> userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();
        userDao = new UserDao();

        banner = findViewById(R.id.banner);
        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        registerUser = findViewById(R.id.registerUser);

        setListeners();
    }

    public void setListeners() {
        banner.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        registerUser.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Name cannot be empty.");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email cannot be empty.");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password cannot be empty.");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters long.");
            editTextPassword.requestFocus();
            return;
        }

        if (!isValidEmailAddress(email)) {
            editTextEmail.setError("Please provide valid Email address.");
            editTextEmail.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set Firebase display name
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            System.out.println("Firebase display name set");
                                        }
                                    }
                                });

                        User user = new User(name, email, mAuth.getCurrentUser().getUid());
                        userDao.add(user);
                        Toast.makeText(RegisterUserActivity.this, "Registration success.",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        // If sign up fails, display a message to the user
                        Toast.makeText(RegisterUserActivity.this, "Registration failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static boolean isValidEmailAddress(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}