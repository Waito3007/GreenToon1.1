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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditToonActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_NEW_COVER = 2;

    private ImageView imageViewToonCover;
    private EditText editTextToonName;
    private EditText editTextToonDesc;
    private Button btnChooseNewCover;
    private Button btnUpdateToon;
    private Button btnDeleteToon;

    private Uri newImageUri;
    private String toonId;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private List<String> selectedGenres = new ArrayList<>();
    private LinearLayout tagContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_toon);

        // Initialize Firebase Database and Storage
        databaseReference = FirebaseDatabase.getInstance().getReference("toons");
        storageReference = FirebaseStorage.getInstance().getReference("toon_covers");

        // Get data from intent
        Intent intent = getIntent();
        toonId = intent.getStringExtra("toonId");

        imageViewToonCover = findViewById(R.id.imageViewToonCover);
        editTextToonName = findViewById(R.id.editTextToonName);
        editTextToonDesc = findViewById(R.id.editTextToonDesc);
        btnChooseNewCover = findViewById(R.id.btnChooseNewCover);
        btnUpdateToon = findViewById(R.id.btnUpdateToon);
        btnDeleteToon = findViewById(R.id.btnDeleteToon);
        tagContainer = findViewById(R.id.tagContainer);

        // Load existing toon data
        loadToonData();
        loadToonTags();

        btnChooseNewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooserForNewCover();
            }
        });

        btnUpdateToon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateToon();
            }
        });

        btnDeleteToon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteToon();
            }
        });
    }

    private void loadToonData() {
        databaseReference.child(toonId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String toonName = snapshot.child("toonName").getValue(String.class);
                String toonDesc = snapshot.child("toonDes").getValue(String.class);
                String toonCover = snapshot.child("toonCover").getValue(String.class);

                editTextToonName.setText(toonName);
                editTextToonDesc.setText(toonDesc);

                // Load existing toon cover image using Glide
                Glide.with(EditToonActivity.this)
                        .load(toonCover)
                        .into(imageViewToonCover);
            }
        }).addOnFailureListener(e -> {
            // Handle error
            Toast.makeText(EditToonActivity.this, "Failed to load toon data", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadToonTags() {
        databaseReference.child(toonId).child("genres").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                for (DataSnapshot genreSnapshot : snapshot.getChildren()) {
                    String genre = genreSnapshot.getKey();
                    // Hiển thị thể loại đã chọn
                    addTag(genre);
                    // Đánh dấu thể loại đã chọn
                    selectedGenres.add(genre);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle error
            Toast.makeText(EditToonActivity.this, "Failed to load toon tags", Toast.LENGTH_SHORT).show();
        });
    }

    private void openFileChooserForNewCover() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_NEW_COVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_NEW_COVER && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newImageUri = data.getData();
            // Display the new image if desired
            // imageViewToonCover.setImageURI(newImageUri);
        }
    }

    private void updateToon() {
        // Update toon information on Firebase Database
        String newName = editTextToonName.getText().toString().trim();
        String newDesc = editTextToonDesc.getText().toString().trim();

        if (newImageUri != null) {
            // If a new image is chosen, update the new image to Firebase Storage
            updateToonCover(newImageUri, newName, newDesc);
        } else {
            // If no new image, only update other information
            databaseReference.child(toonId).child("toonName").setValue(newName);
            databaseReference.child(toonId).child("toonDes").setValue(newDesc);
            updateToonTags(); // Update toon tags
        }
    }

    private void updateToonCover(Uri newImageUri, final String newName, final String newDesc) {
        // Upload the new image to Firebase Storage
        StorageReference fileReference = storageReference.child(toonId).child(System.currentTimeMillis() + ".jpg");

        fileReference.putFile(newImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the URL of the uploaded image
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String newImageUrl = uri.toString();

                        // Update toon information with the new image
                        databaseReference.child(toonId).child("toonCover").setValue(newImageUrl);
                        databaseReference.child(toonId).child("toonName").setValue(newName);
                        databaseReference.child(toonId).child("toonDes").setValue(newDesc);
                        updateToonTags(); // Update toon tags
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditToonActivity.this, "Upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateToonTags() {
        Map<String, Object> genresMap = new HashMap<>();
        for (String genre : selectedGenres) {
            genresMap.put(genre, true);
        }

        // Cập nhật thể loại của toon
        databaseReference.child(toonId).child("genres").updateChildren(genresMap)
                .addOnSuccessListener(aVoid -> {
                    // Thành công
                    Toast.makeText(EditToonActivity.this, "Toon tags updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                    Toast.makeText(EditToonActivity.this, "Failed to update toon tags", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteToon() {
        // Delete the toon from Firebase Database
        databaseReference.child(toonId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditToonActivity.this, "Toon deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditToonActivity.this, "Failed to delete toon. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to add a tag to the tagContainer
    private void addTag(final String tag) {
        final TextView textView = new TextView(this);
        textView.setText(tag);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.bgtag); // Set default background drawable for tag
        textView.setPadding(16, 8, 16, 8);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 8, 8, 8);
        textView.setLayoutParams(layoutParams);
        tagContainer.addView(textView);

        // Set OnClickListener for the tag
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the tag is already selected
                if (selectedGenres.contains(tag)) {
                    // If selected, remove it from the list and UI
                    selectedGenres.remove(tag);
                    textView.setBackgroundColor(Color.BLACK);
                    updateTagStatus(tag, false); // Update the tag status in database
                } else {
                    // If not selected, add it to the list and UI
                    selectedGenres.add(tag);
                    textView.setBackgroundColor(Color.BLUE); // Set background color to blue
                    updateTagStatus(tag, true); // Update the tag status in database
                }
            }
        });
    }

    // Method to update the status of a tag in the database
    private void updateTagStatus(String tag, boolean status) {
        databaseReference.child(toonId).child("genres").child(tag).setValue(status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Tag status updated successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update tag status
                    }
                });
    }
}
