package com.example.firebase_chat;

import static android.app.Activity.RESULT_OK;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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
    TextView bio;
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

        profilePicture.setOnClickListener(this);
        editBio.setOnClickListener(this);


        // Load profile picture from Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileReference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
        profileReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profilePicture));

        // TODO: load name and other info
        userDao = new UserDao();
        userDao.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot user: snapshot.getChildren()){
                    if(user.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        retrievedUser = user.getValue(User.class);
                        name.setText(retrievedUser.name);

                        // If user has bio, load it
                        if(retrievedUser.bio != null){
                            bio.setText(retrievedUser.bio);
                        }

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profilePicture:
                choosePicture();
                break;
            case R.id.editBio:
                // TODO: add a edit bio page
                updateBio();
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
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(profilePicture);  // update profile pic
            });
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload.", Toast.LENGTH_SHORT).show());
    }

    private void updateBio(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bio");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO: save to firebase
                retrievedUser.bio = input.getText().toString();
                userDao.add(retrievedUser);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();

    }

}