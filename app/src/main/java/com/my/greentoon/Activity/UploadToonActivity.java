package com.my.greentoon.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

public class UploadToonActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewToonCover;
    private EditText editTextToonName;
    private EditText editTextToonDesc;
    private Button btnChooseImage;
    private Button btnUploadToon;

    private Uri imageUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_toon);

        // Initialize Firebase Database and Storage
        databaseReference = FirebaseDatabase.getInstance().getReference("toons");
        storageReference = FirebaseStorage.getInstance().getReference("toon_covers");

        imageViewToonCover = findViewById(R.id.imageViewToonCover);
        editTextToonName = findViewById(R.id.editTextToonName);
        editTextToonDesc = findViewById(R.id.editTextToonDesc);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUploadToon = findViewById(R.id.btnUploadToon);

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnUploadToon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToon();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewToonCover.setImageURI(imageUri);
        }
    }

    private void uploadToon() {
        if (imageUri != null) {
            // Tạo một khóa mới sử dụng push()
            DatabaseReference newToonRef = databaseReference.push();
            String toonId = newToonRef.getKey(); // Lấy khóa mới tạo

            // Upload image to Firebase Storage
            StorageReference fileReference = storageReference.child(toonId).child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the URL of the uploaded image
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    // Create Toon object
                                    String toonName = editTextToonName.getText().toString().trim();
                                    String toonDesc = editTextToonDesc.getText().toString().trim();

                                    Toon newToon = new Toon(toonId, imageUrl, toonName, toonDesc);

                                    // Save Toon object to Firebase Database
                                    newToonRef.setValue(newToon);

                                    Toast.makeText(UploadToonActivity.this, "Toon uploaded successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadToonActivity.this, "Upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
        }
    }
}
