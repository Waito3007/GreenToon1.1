package com.my.greentoon.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.my.greentoon.Model.Chapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditChapterActivity extends AppCompatActivity {

    private Spinner spinnerChapterList;
    private EditText editTextChapterName;
    private EditText editTextChapterTitle;
    private Button btnUpdateChapter;
    private Button btBack,btnDeleteChapter;
    private Button btnChooseImages;

    private DatabaseReference chaptersRef;
    private List<Chapter> chapterList;
    private ArrayAdapter<Chapter> adapter;
    private ProgressDialog progressDialog;
    private Toon selectedToon;
    private List<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);

        spinnerChapterList = findViewById(R.id.spinnerChapterList);
        editTextChapterName = findViewById(R.id.editTextChapterName);
        btnDeleteChapter = findViewById(R.id.btnDeleteChapter);
        editTextChapterTitle = findViewById(R.id.editTextChapterTitle);
        btnUpdateChapter = findViewById(R.id.btnUpdateChapter);
        btnChooseImages = findViewById(R.id.btnChooseImages);
        btBack = findViewById(R.id.btBack);
        chaptersRef = FirebaseDatabase.getInstance().getReference().child("chapters");
        chapterList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chapterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChapterList.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Chapter...");
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("toonId")) {
            String toonId = intent.getStringExtra("toonId");
            loadChapters(toonId);
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
                    Toast.makeText(EditChapterActivity.this, "Failed to load selected toon: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        btBack.setOnClickListener(new View.OnClickListener() {
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

        btnUpdateChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateChapter();
            }

        });
        btnDeleteChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteChapter();
            }
        });


        spinnerChapterList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Chapter selectedChapter = (Chapter) parent.getItemAtPosition(position);
                if (selectedChapter != null) {
                    editTextChapterName.setText(selectedChapter.getChapterName());
                    editTextChapterTitle.setText(selectedChapter.getChapterTitle());
                    imageUris.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadChapters(String toonId) {
        chaptersRef.child(toonId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chapter chapter = snapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        chapterList.add(chapter);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditChapterActivity.this, "Failed to load chapters: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    // Trong phương thức updateChapter()
    private void updateChapter() {
        String chapterName = editTextChapterName.getText().toString().trim();
        String chapterTitle = editTextChapterTitle.getText().toString().trim();

        Chapter selectedChapter = (Chapter) spinnerChapterList.getSelectedItem();
        if (selectedChapter == null) {
            Toast.makeText(this, "Please select a chapter", Toast.LENGTH_SHORT).show();
            return;
        }

        String chapterId = selectedChapter.getChapterId();
        String toonId = selectedChapter.getToonId();

        if (TextUtils.isEmpty(chapterName) && TextUtils.isEmpty(chapterTitle) && imageUris.isEmpty()) {
            Toast.makeText(this, "Please make some changes to update", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        Map<String, Object> updateMap = new HashMap<>();
        if (!TextUtils.isEmpty(chapterName)) {
            updateMap.put("chapterName", chapterName);
        }
        if (!TextUtils.isEmpty(chapterTitle)) {
            updateMap.put("chapterTitle", chapterTitle);
        }

        if (!imageUris.isEmpty()) {
            uploadImagesToStorage(imageUris, new OnImagesUploadedListener() {
                @Override
                public void onImagesUploaded(List<String> imageUrls) {
                    updateMap.put("listImgChapter", imageUrls);

                    // Tiến hành cập nhật chương sau khi ảnh đã được tải lên Storage
                    chaptersRef.child(toonId).child(chapterId).updateChildren(updateMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditChapterActivity.this, "Chapter updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditChapterActivity.this, "Failed to update chapter: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                @Override
                public void onUploadFailed(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(EditChapterActivity.this, "Failed to upload images: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Không có ảnh mới, chỉ cần cập nhật thông tin chương
            chaptersRef.child(toonId).child(chapterId).updateChildren(updateMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(EditChapterActivity.this, "Chapter updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditChapterActivity.this, "Failed to update chapter: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Phương thức tải ảnh lên Firebase Storage
    private void uploadImagesToStorage(List<Uri> imageUris, OnImagesUploadedListener listener) {
        List<String> imageUrls = new ArrayList<>();
        final int[] uploadedCount = {0};

        for (Uri imageUri : imageUris) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageUri.getLastPathSegment());
            UploadTask uploadTask = storageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Lấy URL của ảnh đã tải lên thành công
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            imageUrls.add(imageUrl);
                            uploadedCount[0]++;

                            // Kiểm tra nếu tất cả ảnh đã được tải lên
                            if (uploadedCount[0] == imageUris.size()) {
                                listener.onImagesUploaded(imageUrls);
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onUploadFailed(e.getMessage());
                }
            });
        }
    }
    private void deleteChapter(Chapter chapter) {
        String toonId = chapter.getToonId();
        String chapterId = chapter.getChapterId();

        chaptersRef.child(toonId).child(chapterId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditChapterActivity.this, "Chapter deleted successfully", Toast.LENGTH_SHORT).show();
                        // Cập nhật lại danh sách chapter sau khi xóa
                        loadChapters(toonId);
                        // Đặt lại các trường nhập liệu và ảnh đã chọn
                        editTextChapterName.setText("");
                        editTextChapterTitle.setText("");
                        imageUris.clear();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditChapterActivity.this, "Failed to delete chapter: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void confirmDeleteChapter() {
        Chapter selectedChapter = (Chapter) spinnerChapterList.getSelectedItem();
        if (selectedChapter == null) {
            Toast.makeText(this, "Please select a chapter to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this chapter?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteChapter(selectedChapter);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    // Interface để lắng nghe việc tải ảnh lên Storage
    interface OnImagesUploadedListener {
        void onImagesUploaded(List<String> imageUrls);
        void onUploadFailed(String errorMessage);
    }

}
