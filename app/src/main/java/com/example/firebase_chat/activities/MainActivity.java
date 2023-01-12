package com.example.firebase_chat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_chat.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button login;
    private TextView register, forgotPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        forgotPassword = findViewById(R.id.forgotPassword);

        setListeners();
    }

    public void setListeners() {
        register.setOnClickListener(view -> startActivity(new Intent(this, RegisterUserActivity.class)));
        login.setOnClickListener(view -> userLogin());
        forgotPassword.setOnClickListener(view -> resetPassword());
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        if (!RegisterUserActivity.isValidEmailAddress(email)) {
            editTextEmail.setError("Please provide valid Email address.");
            editTextEmail.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Redirect to home page
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            } else {
                Toast.makeText(MainActivity.this, "Failed to login, please check your credentials.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassword() {
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required.");
            editTextEmail.requestFocus();
            return;
        }

        if (!RegisterUserActivity.isValidEmailAddress(email)) {
            editTextEmail.setError("Please provide a valid Email address.");
            editTextEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Check your email to reset your password.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Try again, something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}