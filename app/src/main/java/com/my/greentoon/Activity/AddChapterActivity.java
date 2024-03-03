package com.my.greentoon.Activity;

import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.my.greentoon.Adapter.SelectedImageAdapter;
import com.my.greentoon.Model.Chapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    private ProgressDialog progressDialog;

    private RecyclerView recyclerViewSelectedImages;
    private SelectedImageAdapter selectedImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);

        spinnerToonList = findViewById(R.id.spinnerToonList);
        editTextChapterName = findViewById(R.id.editTextChapterName);
        editTextChapterTitle = findViewById(R.id.editTextChapterTitle);
        btnChooseImages = findViewById(R.id.btnChooseImages);
        btnAddChapter = findViewById(R.id.btnAddChapter);

        recyclerViewSelectedImages = findViewById(R.id.recyclerViewSelectedImages);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Chapter...");
        progressDialog.setCancelable(false);

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

        recyclerViewSelectedImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        selectedImageAdapter = new SelectedImageAdapter(this, imageUris);
        recyclerViewSelectedImages.setAdapter(selectedImageAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(imageUris, fromPosition, toPosition);
                selectedImageAdapter.notifyItemMoved(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Handle swipe to delete (if needed)
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewSelectedImages);
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
                    // Thêm ảnh vào đầu danh sách để hiển thị ảnh được chọn trước đầu tiên
                    imageUris.add(0, imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                // Thêm ảnh vào đầu danh sách để hiển thị ảnh được chọn trước đầu tiên
                imageUris.add(0, imageUri);
            }
            Toast.makeText(this, "Images selected: " + imageUris.size(), Toast.LENGTH_SHORT).show();
            selectedImageAdapter.notifyDataSetChanged();
        }
    }

    private void addChapter() {
        String chapterName = editTextChapterName.getText().toString().trim();
        String chapterTitle = editTextChapterTitle.getText().toString().trim();
        Toon selectedToon = (Toon) spinnerToonList.getSelectedItem();

        if (selectedToon != null && !chapterName.isEmpty() && !chapterTitle.isEmpty() && !imageUris.isEmpty()) {
            uploadImages(imageUris, chapterName, chapterTitle, selectedToon);
        } else {
            Toast.makeText(this, "Please fill all fields and choose images", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImages(List<Uri> imageUris, String chapterName, String chapterTitle, Toon selectedToon) {
        progressDialog.show(); // Hiển thị dialog loading khi bắt đầu tải lên

        // Đếm số lượng hình ảnh đã tải lên thành công
        AtomicInteger uploadedCount = new AtomicInteger(0);

        // Duyệt qua danh sách ảnh và tải lên mỗi ảnh theo thứ tự được chọn
        for (int i = 0; i < imageUris.size(); i++) {
            Uri imageUri = imageUris.get(i);
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

                            // Tăng biến đếm số lượng hình ảnh đã tải lên thành công
                            int count = uploadedCount.incrementAndGet();
                            // Kiểm tra xem đã tải lên tất cả các hình ảnh chưa
                            if (count == imageUris.size()) {
                                // Tất cả các hình ảnh đã được tải lên, bạn có thể lưu URL vào Firebase Database
                                saveChapterToDatabase(listImgChapter, chapterName, chapterTitle, selectedToon);
                                // Sau khi tất cả các hình ảnh đã được tải lên, tắt dialog loading
                                progressDialog.dismiss();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý trường hợp tải lên thất bại
                        Toast.makeText(AddChapterActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                String chapterId = chaptersRef.child(toonId).push().getKey();

                DatabaseReference chapterRef = chaptersRef.child(toonId).child(chapterId);

                Chapter chapter = new Chapter(chapterId, chapterName, chapterTitle, imageUrls, (int) newChapterNum);
                chapter.setToonId(toonId);
                chapterRef.setValue(chapter)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddChapterActivity.this, "Chapter added successfully", Toast.LENGTH_SHORT).show();
                            editTextChapterName.setText("");
                            editTextChapterTitle.setText("");
                            imageUris.clear();
                            listImgChapter.clear();
                            selectedImageAdapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddChapterActivity.this, "Failed to add chapter: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddChapterActivity.this, "Failed to get chapter count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
}
