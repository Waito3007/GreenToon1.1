package com.my.greentoon.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

public class UploadStoryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgBookCover;
    private EditText edtStoryName, edtStoryGenre, edtStoryDescription;
    private Button btnChooseCover, btnUpload;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_story);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Toon");

        imgBookCover = findViewById(R.id.imgBookCover);
        edtStoryName = findViewById(R.id.edtStoryName);
        edtStoryGenre = findViewById(R.id.edtStoryGenre);
        edtStoryDescription = findViewById(R.id.edtStoryDescription);
        btnChooseCover = findViewById(R.id.btnChooseCover);
        btnUpload = findViewById(R.id.btnUpload);

        btnChooseCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStory();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Chọn ảnh bìa truyện"), PICK_IMAGE_REQUEST);
    }

    private void uploadStory() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String storyName = edtStoryName.getText().toString().trim();
            String storyGenre = edtStoryGenre.getText().toString().trim();
            String storyDescription = edtStoryDescription.getText().toString().trim();

            if (!storyName.isEmpty() && !storyGenre.isEmpty() && !storyDescription.isEmpty() && selectedImageUri != null) {
                StorageReference imageRef = storageReference.child("bookCovers/" + userId + "_" + System.currentTimeMillis() + ".jpg");

                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String storyBookCover = uri.toString();
                                Toon toon = new Toon(userId, storyName, storyGenre, storyDescription, storyBookCover);

                                // Lưu thông tin truyện vào Firebase Realtime Database
                                DatabaseReference toonRef = databaseReference.child(userId).child(storyName);
                                toonRef.setValue(toon);

                                Toast.makeText(UploadStoryActivity.this, "Đăng truyện thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UploadStoryActivity.this, "Lỗi khi đăng truyện", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(UploadStoryActivity.this, "Vui lòng nhập đủ thông tin và chọn ảnh bìa truyện", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgBookCover.setImageURI(selectedImageUri);
        }
    }
}
