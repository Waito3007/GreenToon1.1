package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.ImageAdapter;
import com.my.greentoon.Model.Chapter;
import com.my.greentoon.R;

import java.util.List;

public class ChapterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        // Lấy chapterId và toonId từ Intent
        Intent intent = getIntent();
        String chapterId = intent.getStringExtra("chapterId");
        String toonId = intent.getStringExtra("toonId");

        // Lấy reference của các thành phần giao diện từ layout
        TextView textViewChapterName = findViewById(R.id.textViewChapterName);
        RecyclerView recyclerViewImages = findViewById(R.id.recyclerViewImages);

        // Sử dụng chapterId để truy xuất dữ liệu chương từ Firebase
        DatabaseReference chapterRef = FirebaseDatabase.getInstance().getReference().child("chapters").child(toonId).child(chapterId);
        chapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lấy dữ liệu chương từ dataSnapshot và hiển thị trên giao diện người dùng
                Chapter chapter = dataSnapshot.getValue(Chapter.class);
                if (chapter != null) {
                    // Hiển thị tên chương và danh sách ảnh trên giao diện người dùng
                    String chapterName = chapter.getChapterName();
                    List<String> listImgChapter = chapter.getListImgChapter();

                    // Hiển thị tên chương vào TextView
                    textViewChapterName.setText(chapterName);

                    // Log danh sách các URL hình ảnh
                    Log.d("ChapterActivity", "Chapter Name: " + chapterName);
                    Log.d("ChapterActivity", "Image URLs: " + listImgChapter);

                    // Khởi tạo và thiết lập Adapter cho RecyclerView
                    ImageAdapter imageAdapter = new ImageAdapter(listImgChapter);
                    recyclerViewImages.setAdapter(imageAdapter);
                    recyclerViewImages.setLayoutManager(new LinearLayoutManager(ChapterActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi không thể truy xuất dữ liệu chương từ Firebase
                Toast.makeText(ChapterActivity.this, "Failed to load chapter: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
