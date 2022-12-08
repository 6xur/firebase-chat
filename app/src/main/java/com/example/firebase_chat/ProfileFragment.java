package com.example.firebase_chat;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    StorageReference storageRef;
    Button deleteAccountBtn;

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
        deleteAccountBtn = view.findViewById(R.id.deleteAccountBtn);

        profilePicture.setOnClickListener(this);
        editBio.setOnClickListener(this);
        editContactInfo.setOnClickListener(this);
        deleteAccountBtn.setOnClickListener(this);

        // Initially invisible, only display these if they are stored in Firebase
        phoneContainer.setVisibility(View.GONE);
        locationContainer.setVisibility(View.GONE);

        // Load profile picture from Firebase Storage
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profilePicture));

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

                            if (retrievedUser.phone != null) {
                                phone.setText(retrievedUser.phone);
                                phoneContainer.setVisibility(View.VISIBLE);
                            } else {
                                phoneContainer.setVisibility(View.GONE);
                            }

                            if (retrievedUser.location != null) {
                                location.setText(retrievedUser.location);
                                locationContainer.setVisibility(View.VISIBLE);
                            } else {
                                locationContainer.setVisibility(View.GONE);
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
                chooseProfilePic();
                break;
            case R.id.editBio:
                updateBio();
                break;
            case R.id.editContactInfo:
                updateContactInfo();
                break;
            case R.id.deleteAccountBtn:
                deleteAccount();  // Delete user from Firebase Auth
                // TODO: remove profile picture
                break;
        }
    }

    private void chooseProfilePic() {
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
                        uploadProfilePic(imageUri);
                    }
                }
            });

    private void uploadProfilePic(Uri imageUri) {
        // Upload picture to Firebase Storage
        StorageReference fileRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(getActivity(), "Profile photo uploaded", Toast.LENGTH_SHORT).show();
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profilePicture));
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload", Toast.LENGTH_SHORT).show());
    }

    // Delete profile pic of current user
    private void deleteProfilePic(){
        StorageReference fileRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
        fileRef.delete();
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

    private void updateContactInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Contact info");

        EditText phoneInput = new EditText(getActivity());
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneInput.setHint("Phone");

        EditText locationInput = new EditText(getActivity());
        locationInput.setHint("Location");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(phoneInput);
        layout.addView(locationInput);
        layout.setPadding(50, 0, 50, 0);
        builder.setView(layout);

        if (retrievedUser.phone != null) {
            phoneInput.setText(retrievedUser.phone);
        }
        if (retrievedUser.location != null) {
            locationInput.setText(retrievedUser.location);
        }

        builder.setPositiveButton("Ok", ((dialogInterface, i) -> {
            if (phoneInput.getText().toString().length() > 0) {
                retrievedUser.phone = phoneInput.getText().toString();
            } else {
                retrievedUser.phone = null;
            }
            if (locationInput.getText().toString().length() > 0) {
                retrievedUser.location = locationInput.getText().toString();
            } else {
                retrievedUser.location = null;
            }
            userDao.add(retrievedUser);
        }));
        builder.setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.cancel()));

        builder.show();
    }

    private void deleteAccount(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        deleteProfilePic();  // delete profile pic
                        user.delete();  // delete user from Firebase Auth
                        userDao.delete();  // delete user info from realtime database
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        Toast.makeText(getActivity(), "Account deleted.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}