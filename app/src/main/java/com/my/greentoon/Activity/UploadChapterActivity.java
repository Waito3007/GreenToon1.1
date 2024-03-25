package com.my.greentoon.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.my.greentoon.Model.Chapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class UploadChapterActivity extends AppCompatActivity {

    private Spinner spinnerToonList;
    private EditText editTextChapterName;
    private EditText editTextChapterTitle;
    private Button btnChooseImages;
    private Button btnUploadChapter;

    private Button btnBack;
    private List<Uri> imageUris = new ArrayList<>();
    private List<String> listImgChapter = new ArrayList<>();

    private ProgressDialog progressDialog;

    private DatabaseReference chaptersRef;
    private Toon selectedToon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_chapter);


        editTextChapterName = findViewById(R.id.editTextChapterName);
        editTextChapterTitle = findViewById(R.id.editTextChapterTitle);
        btnChooseImages = findViewById(R.id.btnChooseImages);
        btnUploadChapter = findViewById(R.id.btnUploadChapter);
        btnBack = findViewById(R.id.btBack);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Chapter...");
        progressDialog.setCancelable(false);

        chaptersRef = FirebaseDatabase.getInstance().getReference("chapters");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("toonId")) {
            String toonId = intent.getStringExtra("toonId");
            DatabaseReference toonRef = FirebaseDatabase.getInstance().getReference("toons").child(toonId);
            toonRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    selectedToon = dataSnapshot.getValue(Toon.class);
                    // hien thi view anh bia va name truyen trong layout
                    if (dataSnapshot.exists()) {
                        String toonName = dataSnapshot.child("toonName").getValue(String.class);
                        String toonCover=dataSnapshot.child("toonCover").getValue(String.class);
                        // Hiển thị len
                        TextView tvtoonName = findViewById(R.id.tvtoonName);
                        tvtoonName.setText(toonName);
                        ImageView imgtoonCover = findViewById(R.id.imgtoonCover);
                        Picasso.get().load(toonCover).into(imgtoonCover);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UploadChapterActivity.this, "Failed to load selected toon: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnChooseImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImages();
            }
        });

        btnUploadChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadChapter();
            }
        });
    }
    private void chooseImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
            }
        }
    }

    private void uploadChapter() {
        String chapterName = editTextChapterName.getText().toString().trim();
        String chapterTitle = editTextChapterTitle.getText().toString().trim();

        if (selectedToon != null && !chapterName.isEmpty() && !chapterTitle.isEmpty() && !imageUris.isEmpty()) {
            uploadImages(imageUris, chapterName, chapterTitle, selectedToon);
        } else {
            Toast.makeText(this, "Please fill all fields and choose images", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImages(List<Uri> imageUris, String chapterName, String chapterTitle, Toon selectedToon) {
        progressDialog.show();
        AtomicInteger uploadedCount = new AtomicInteger(0);
        for (int i = 0; i < imageUris.size(); i++) {
            Uri imageUri = imageUris.get(i);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(Objects.requireNonNull(imageUri.getLastPathSegment()));
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            listImgChapter.add(downloadUrl);
                            int count = uploadedCount.incrementAndGet();
                            if (count == imageUris.size()) {
                                saveChapterToDatabase(listImgChapter, chapterName, chapterTitle, selectedToon);
                                progressDialog.dismiss();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UploadChapterActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveChapterToDatabase(List<String> imageUrls, String chapterName, String chapterTitle, Toon selectedToon) {
        String toonId = selectedToon.getToonId();
        DatabaseReference toonChapterRef = chaptersRef.child(toonId);
        List<Long> existingChapterNums = new ArrayList<>();
        toonChapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chapter chapter = snapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        existingChapterNums.add((long) chapter.getNumChapter());
                    }
                }
                long newChapterNum = findNextChapterNum(existingChapterNums);
                String
                        chapterId = chaptersRef.child(toonId).push().getKey();
                DatabaseReference chapterRef = chaptersRef.child(toonId).child(chapterId);
                Chapter chapter = new Chapter(chapterId, chapterName, chapterTitle, imageUrls, (int) newChapterNum);
                chapter.setToonId(toonId);
                chapterRef.setValue(chapter)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(UploadChapterActivity.this, "Chapter added successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UploadChapterActivity.this, "Failed to add chapter: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UploadChapterActivity.this, "Failed to get chapter count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long findNextChapterNum(List<Long> existingChapterNums) {
        long nextChapterNum = 1;
        while (existingChapterNums.contains(nextChapterNum)) {
            nextChapterNum++;
        }
        return nextChapterNum;
    }

    private void clearFields() {
        editTextChapterName.setText("");
        editTextChapterTitle.setText("");
        imageUris.clear();
        listImgChapter.clear();
    }
}
