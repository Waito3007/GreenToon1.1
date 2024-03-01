package com.my.greentoon.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class AddChapterActivity extends AppCompatActivity {

    private Spinner spinnerToonList;
    private EditText editTextChapterName;
    private EditText editTextChapterTitle;
    private Button btnChooseImages;
    private Button btnAddChapter;

    private List<Toon> toonList;
    private DatabaseReference chaptersRef;
    private List<Uri> imageUris = new ArrayList<>();
    private List<String> listImgChapter = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);

        spinnerToonList = findViewById(R.id.spinnerToonList);
        editTextChapterName = findViewById(R.id.editTextChapterName);
        editTextChapterTitle = findViewById(R.id.editTextChapterTitle);
        btnChooseImages = findViewById(R.id.btnChooseImages);
        btnAddChapter = findViewById(R.id.btnAddChapter);

        chaptersRef = FirebaseDatabase.getInstance().getReference("chapters");

        toonList = new ArrayList<>();

        final ArrayAdapter<Toon> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, toonList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerToonList.setAdapter(adapter);

        spinnerToonList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toon selectedToon = (Toon) parent.getItemAtPosition(position);
                if (selectedToon != null) {
                    Toast.makeText(AddChapterActivity.this, "Selected Toon: " + selectedToon.getToonName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Đọc danh sách các bộ truyện từ Firebase
        DatabaseReference toonsRef = FirebaseDatabase.getInstance().getReference("toons");
        toonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Toon toon = snapshot.getValue(Toon.class);
                    if (toon != null) {
                        toonList.add(toon);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddChapterActivity.this, "Failed to load toons: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnChooseImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImages();
            }
        });

        btnAddChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChapter();
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
            Toast.makeText(this, "Images selected: " + imageUris.size(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addChapter() {
        String chapterName = editTextChapterName.getText().toString().trim();
        String chapterTitle = editTextChapterTitle.getText().toString().trim();
        Toon selectedToon = (Toon) spinnerToonList.getSelectedItem();

        if (selectedToon != null && !chapterName.isEmpty() && !chapterTitle.isEmpty() && !imageUris.isEmpty()) {
            // Tải danh sách hình ảnh lên Firebase Storage và sau đó lưu URL của các hình ảnh vào Firebase Database
            uploadImages(imageUris, chapterName, chapterTitle, selectedToon);
        } else {
            Toast.makeText(this, "Please fill all fields and choose images", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImages(List<Uri> imageUris, String chapterName, String chapterTitle, Toon selectedToon) {
        if (!imageUris.isEmpty()) {
            for (Uri imageUri : imageUris) {
                // Tạo một tham chiếu đến hình ảnh trong Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(imageUri.getLastPathSegment());

                // Tải hình ảnh lên Firebase Storage
                storageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Lấy URL của hình ảnh đã được tải lên
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Sau khi tải lên thành công, bạn có thể sử dụng URI để lưu vào Firebase Realtime Database hoặc Cloud Firestore
                                String downloadUrl = uri.toString();
                                // Thêm URL của hình ảnh vào danh sách
                                listImgChapter.add(downloadUrl);
                                // Kiểm tra xem đã tải lên tất cả các hình ảnh chưa
                                if (listImgChapter.size() == imageUris.size()) {
                                    // Tất cả các hình ảnh đã được tải lên, bạn có thể lưu URL vào Firebase Database
                                    saveChapterToDatabase(listImgChapter, chapterName, chapterTitle, selectedToon);
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Xử lý trường hợp tải lên thất bại
                            Toast.makeText(AddChapterActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    private void saveChapterToDatabase(List<String> imageUrls, String chapterName, String chapterTitle, Toon selectedToon) {
        // Lấy toonId của bộ truyện được chọn
        String toonId = selectedToon.getToonId();

        // Tạo một chapterId duy nhất cho chương
        String chapterId = chaptersRef.child(toonId).push().getKey();

        // Tạo một tham chiếu đến nút chương mới được tạo
        DatabaseReference chapterRef = chaptersRef.child(toonId).child(chapterId);

        // Lưu dữ liệu chương vào Firebase Database
        chapterRef.child("chapterId").setValue(chapterId); // Lưu chapterId
        chapterRef.child("chapterName").setValue(chapterName);
        chapterRef.child("chapterTitle").setValue(chapterTitle);
        chapterRef.child("listImgChapter").setValue(imageUrls)
                .addOnSuccessListener(aVoid -> {
                    // Đã lưu thành công
                    Toast.makeText(AddChapterActivity.this, "Chapter added successfully", Toast.LENGTH_SHORT).show();
                    // Reset các trường nhập liệu
                    editTextChapterName.setText("");
                    editTextChapterTitle.setText("");
                    imageUris.clear();
                })
                .addOnFailureListener(e -> {
                    // Lỗi xảy ra khi lưu dữ liệu
                    Toast.makeText(AddChapterActivity.this, "Failed to add chapter: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
