package com.my.greentoon.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.my.greentoon.Model.FollowedToon;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadToonActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewToonCover;
    private EditText editTextToonName;
    private EditText editTextToonDesc;
    private Button btnChooseImage;
    private Button btnUploadToon;

    private Button btBack;

    private LinearLayout tagContainer;
    private List<String> selectedGenres = new ArrayList<>();
    private List<String> allGenres = Arrays.asList("Action", "Drama", "Fantasy", "Chuyển Sinh", "Ngôn Tình", "Trinh thám", "Tu Tiên", "Manhwa");
    private Uri imageUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_toon);

        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database and Storage
        databaseReference = FirebaseDatabase.getInstance().getReference("toons");
        storageReference = FirebaseStorage.getInstance().getReference("toon_covers");

        btBack = findViewById(R.id.btBack);
        imageViewToonCover = findViewById(R.id.imageViewToonCover);
        editTextToonName = findViewById(R.id.editTextToonName);
        editTextToonDesc = findViewById(R.id.editTextToonDesc);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUploadToon = findViewById(R.id.btnUploadToon);
        tagContainer = findViewById(R.id.tagContainer);

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUploadToon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToon();
            }
        });

        // Add tags
        addTag("Action");
        addTag("Drama");
        addTag("Fantasy");
        addTag("Chuyển Sinh");
        addTag("Ngôn Tình");
        addTag("Trinh thám");
        addTag("Tu Tiên");
        addTag("Manhwa");
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
            DatabaseReference newToonRef = databaseReference.push();
            String toonId = newToonRef.getKey();

            StorageReference fileReference = storageReference.child(toonId).child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            String toonName = editTextToonName.getText().toString().trim();
                            String toonDesc = editTextToonDesc.getText().toString().trim();

                            // Tạo map thể loại
                            Map<String, Boolean> genresMap = new HashMap<>();
                            for (String genre : selectedGenres) {
                                genresMap.put(genre, true);
                            }

                            // Bổ sung các thể loại không được chọn với giá trị false
                            for (String genre : allGenres) {
                                if (!selectedGenres.contains(genre)) {
                                    genresMap.put(genre, false);
                                }
                            }

                            Toon newToon = new Toon(toonId, imageUrl, toonName, toonDesc);
                            newToon.setGenres(genresMap); // Set genres to the newToon object

                            // Lưu vào db
                            newToonRef.setValue(newToon).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UploadToonActivity.this, "Toon uploaded successfully", Toast.LENGTH_SHORT).show();
                                    // Thêm truyện vào danh sách đã theo dõi của người dùng
                                    addToFollowedList(toonId);
                                    finish();
                                } else {
                                    Toast.makeText(UploadToonActivity.this, "Failed to upload toon", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UploadToonActivity.this, "Upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFollowedList(String toonId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference followedToonRef = FirebaseDatabase.getInstance().getReference().child("followed_toons");
            FollowedToon followedToon = new FollowedToon(userId, toonId, true); // Đánh dấu truyện là yêu thích khi thêm vào
            followedToonRef.push().setValue(followedToon)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UploadToonActivity.this, "Truyện đã được thêm vào danh sách yêu thích của bạn", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UploadToonActivity.this, "Đã xảy ra lỗi khi thêm truyện vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    // Hàm addtag
    private void addTag(final String tag) {
        final TextView textView = new TextView(this);
        textView.setText(tag);
        textView.setTextColor(Color.GRAY);
        textView.setBackgroundResource(R.drawable.bgtag); // Set default background drawable for tag
        textView.setPadding(16, 8, 16, 8);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 8, 8, 8);
        textView.setLayoutParams(layoutParams);
        tagContainer.addView(textView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedGenres.contains(tag)) {
                    selectedGenres.remove(tag);
                    textView.setBackgroundColor(Color.BLACK);
                } else {
                    selectedGenres.add(tag);
                    textView.setBackgroundColor(Color.BLUE);
                }
            }
        });
    }
}
