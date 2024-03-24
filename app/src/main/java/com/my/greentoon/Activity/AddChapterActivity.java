    package com.my.greentoon.Activity;

    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextWatcher;
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
        private EditText editTextSearchToon; // Thêm EditText cho việc tìm kiếm Toon

        private List<Toon> toonList;
        private DatabaseReference chaptersRef;
        private List<Uri> imageUris = new ArrayList<>();
        private List<String> listImgChapter = new ArrayList<>();

        private ProgressDialog progressDialog;

        private RecyclerView recyclerViewSelectedImages;
        private SelectedImageAdapter selectedImageAdapter;
        private ArrayAdapter<Toon> adapter; // Chuyển adapter thành biến toàn cục để truy cập từ phương thức khác

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
            editTextSearchToon = findViewById(R.id.editTextSearchToon); // Ánh xạ EditText tìm kiếm Toon

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Adding Chapter...");
            progressDialog.setCancelable(false);
            chaptersRef = FirebaseDatabase.getInstance().getReference("chapters");
            toonList = new ArrayList<>();

            // Khởi tạo adapter và ánh xạ với Spinner
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, toonList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerToonList.setAdapter(adapter);

            // Xử lý sự kiện tìm kiếm Toon khi nhập văn bản vào EditText
            editTextSearchToon.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchText = s.toString().toLowerCase();
                    List<Toon> filteredToonList = new ArrayList<>();
                    for (Toon toon : toonList) {
                        if (toon.getToonName().toLowerCase().contains(searchText)) {
                            filteredToonList.add(toon);
                        }
                    }
                    adapter.clear();
                    adapter.addAll(filteredToonList);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });


            // Lắng nghe sự kiện khi chọn một Toon từ Spinner
            spinnerToonList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Toon selectedToon = (Toon) parent.getItemAtPosition(position);
                    if (selectedToon != null) {
                        Toast.makeText(AddChapterActivity.this, "Selected Toon: " + selectedToon.getToonName(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // Lấy danh sách Toon từ Firebase và cập nhật Spinner
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

            // Xử lý sự kiện chọn hình ảnh
            btnChooseImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseImages();
                }
            });

            // Xử lý sự kiện thêm Chapter
            btnAddChapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addChapter();
                }
            });

            // Cài đặt RecyclerView cho danh sách hình ảnh được chọn
            recyclerViewSelectedImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            selectedImageAdapter = new SelectedImageAdapter(this, imageUris);
            recyclerViewSelectedImages.setAdapter(selectedImageAdapter);

            // Cài đặt ItemTouchHelper cho RecyclerView để xử lý sự kiện kéo thả
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull
                RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
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
                        // Add the image to the beginning of the list to display the selected image first
                        imageUris.add(0, imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    // Add the image to the beginning of the list to display the selected image first
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
            progressDialog.show(); // Display the loading dialog when starting to upload
            // Count the number of successfully uploaded images
            AtomicInteger uploadedCount = new AtomicInteger(0);
            // Iterate through the list of images and upload each image in the selected order
            for (int i = 0; i < imageUris.size(); i++) {
                Uri imageUri = imageUris.get(i);
                // Create a reference to the image in Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(imageUri.getLastPathSegment());
                // Upload the image to Firebase Storage
                storageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Get the URL of the uploaded image
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // After successfully uploading, you can use the URI to save to Firebase Realtime Database or Cloud Firestore
                                String downloadUrl = uri.toString();
                                // Add the URL of the image to the list
                                listImgChapter.add(downloadUrl);
                                // Increase the count of successfully uploaded images
                                int count = uploadedCount.incrementAndGet();
                                // Check if all images have been uploaded
                                if (count == imageUris.size()) {
                                    // All images have been uploaded, you can save the URLs to the Firebase Database
                                    saveChapterToDatabase(listImgChapter, chapterName, chapterTitle, selectedToon);
                                    // After all images have been uploaded, dismiss the loading dialog
                                    progressDialog.dismiss();
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Handle the case of upload failure
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
