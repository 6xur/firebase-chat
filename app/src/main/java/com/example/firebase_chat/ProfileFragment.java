package com.example.firebase_chat;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    Uri imageUri;
    ImageView profilePicture;
    TextView name;
    TextView editBio;
    TextView editContactInfo;
    TextView bio;
    TextView phone, email, location;
    LinearLayout phoneContainer, locationContainer;
    StorageReference storageReference;

    User retrievedUser;
    UserDao userDao;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePicture = view.findViewById(R.id.profilePicture);
        name = view.findViewById(R.id.name);
        editBio = view.findViewById(R.id.editBio);
        bio = view.findViewById(R.id.bio);
        phone = view.findViewById(R.id.phone);
        phoneContainer = view.findViewById(R.id.phoneContainer);
        email = view.findViewById(R.id.email);
        location = view.findViewById(R.id.location);
        locationContainer = view.findViewById(R.id.locationContainer);
        editContactInfo = view.findViewById(R.id.editContactInfo);

        profilePicture.setOnClickListener(this);
        editBio.setOnClickListener(this);
        editContactInfo.setOnClickListener(this);

        // Initially invisible, only display these if they are stored in Firebase
        phoneContainer.setVisibility(View.GONE);
        locationContainer.setVisibility(View.GONE);

        // Load profile picture from Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileReference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
        profileReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profilePicture));

        // Update user info in real time
        userDao = new UserDao();
        userDao.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (user.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        retrievedUser = user.getValue(User.class);
                        if (retrievedUser != null) {
                            name.setText(retrievedUser.name);
                            email.setText(retrievedUser.email);

                            if (retrievedUser.bio != null) {
                                bio.setText(retrievedUser.bio);
                            }

                            if (retrievedUser.phone != null){
                                phone.setText(retrievedUser.phone);
                                phoneContainer.setVisibility(View.VISIBLE);
                            }

                            if (retrievedUser.location != null){
                                location.setText(retrievedUser.location);
                                locationContainer.setVisibility(View.VISIBLE);
                            }

                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profilePicture:
                choosePicture();
                break;
            case R.id.editBio:
                updateBio();
                break;
            case R.id.editContactInfo:
                updateContactInfo();
                break;
        }
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ChoosePictureLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> ChoosePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        imageUri = result.getData().getData();
                        uploadPicture(imageUri);
                    }
                }
            });

    private void uploadPicture(Uri imageUri) {
        // Upload picture to Firebase Storage
        StorageReference fileReference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(getActivity(), "Profile photo uploaded", Toast.LENGTH_SHORT).show();
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profilePicture));
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload", Toast.LENGTH_SHORT).show());
    }

    private void updateBio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bio");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            retrievedUser.bio = input.getText().toString();
            userDao.add(retrievedUser);
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

        builder.show();
    }

    private void updateContactInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Contact info");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText phoneInput = new EditText(getActivity());
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneInput.setHint("Phone");

        EditText locationInput = new EditText(getActivity());
        locationInput.setHint("Location");

        layout.addView(phoneInput);
        layout.addView(locationInput);
        layout.setPadding(50,0,50,0);
        builder.setView(layout);

        if(retrievedUser.phone != null){
            phoneInput.setText(retrievedUser.phone);
        }
        if(retrievedUser.location != null){
            locationInput.setText(retrievedUser.location);
        }

        builder.setPositiveButton("Ok", ((dialogInterface, i) -> {
            if(phoneInput.getText().toString().length() > 0){
                retrievedUser.phone = phoneInput.getText().toString();
            }
            if(locationInput.getText().toString().length() > 0){
                retrievedUser.location = locationInput.getText().toString();
            }
            userDao.add(retrievedUser);
        }));
        builder.setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.cancel()));

        builder.show();
    }

}