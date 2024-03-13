package com.my.greentoon.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.my.greentoon.R;

public class EditAvatarActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgAvatar;
    private Button btChangeAvatar, btSave;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_avatar);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        imgAvatar = findViewById(R.id.imgAvatar);
        btChangeAvatar = findViewById(R.id.btChangeAvatar);
        btSave = findViewById(R.id.btSave);

        btChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAvatar();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    private void saveAvatar() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Nếu có chọn ảnh mới, upload ảnh lên Firebase Storage và cập nhật đường dẫn vào Realtime Database
            if (selectedImageUri != null) {
                uploadImage(selectedImageUri, userId);
            } else {
                // Nếu không có ảnh mới, có thể thực hiện các bước khác (nếu cần)
                Toast.makeText(EditAvatarActivity.this, "Không có ảnh mới để lưu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(Uri imageUri, String userId) {
        StorageReference imageRef = storageReference.child("avatars/" + userId + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy đường dẫn ảnh sau khi upload thành công
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Cập nhật đường dẫn ảnh vào Realtime Database
                        String imageUrl = uri.toString();
                        databaseReference.child(userId).child("avatarUser").setValue(imageUrl);

                        // Hiển thị ảnh (Ở đây là hiển thị trong ImageView, bạn có thể làm theo ý muốn)
                        imgAvatar.setImageURI(imageUri);

                        Toast.makeText(EditAvatarActivity.this, "Lưu ảnh thành công", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditAvatarActivity.this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
        }
    }
}
