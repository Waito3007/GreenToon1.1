package com.my.greentoon.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.my.greentoon.Model.Status;
import com.my.greentoon.R;

import java.util.UUID;

public class UploadStoryFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextTitle;
    private ImageView imageViewStory;
    private Button buttonChooseImage;
    private Button buttonUploadStory;

    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference statusRef;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    public UploadStoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload_story, container, false);

        mAuth = FirebaseAuth.getInstance();
        statusRef = FirebaseDatabase.getInstance().getReference().child("status");
        storageReference = FirebaseStorage.getInstance().getReference().child("status_images");

        editTextTitle = rootView.findViewById(R.id.editTextTitle);
        imageViewStory = rootView.findViewById(R.id.imageViewStory);
        buttonChooseImage = rootView.findViewById(R.id.buttonChooseImage);
        buttonUploadStory = rootView.findViewById(R.id.buttonUploadStory);

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonUploadStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStory();
            }
        });

        return rootView;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewStory.setImageURI(imageUri);
        }
    }

    private void uploadStory() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading story...");
        progressDialog.show();

        String title = editTextTitle.getText().toString().trim();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (title.isEmpty() || imageUri == null || currentUser == null) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        // Tạo một tên duy nhất cho ảnh story
        final String imageName = UUID.randomUUID().toString();
        StorageReference fileReference = storageReference.child(imageName);

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy đường dẫn của ảnh đã tải lên
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Tạo một status mới với thông tin của story và tải lên database
                        Status status = new Status();
                        status.setStatusTitle(title);
                        status.setStatusContent(uri.toString());
                        status.setUserId(currentUser.getUid());

                        String statusId = statusRef.push().getKey();
                        status.setStatusId(statusId);

                        statusRef.child(statusId).setValue(status)
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Story uploaded successfully", Toast.LENGTH_SHORT).show();
                                    // Kết thúc Fragment sau khi upload xong
                                    getActivity().getSupportFragmentManager().popBackStack();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Failed to upload story", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to upload story", Toast.LENGTH_SHORT).show();
                });
    }
}
