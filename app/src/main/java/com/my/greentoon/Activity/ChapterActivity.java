package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.ImageAdapter;
import com.my.greentoon.Fragment.CommentFragment;
import com.my.greentoon.Model.Chapter;
import com.my.greentoon.R;

import java.util.List;

public class ChapterActivity extends AppCompatActivity {

    private String toonId;
    private String currentChapterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        // Get toonId from Intent
        Intent intent = getIntent();
        toonId = intent.getStringExtra("toonId");

        if (toonId != null) {
            // Continue with activity initialization
            initializeActivity();
        } else {
            // Handle case where toonId is missing
            Toast.makeText(this, "Missing toonId", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }
    }

    private void initializeActivity() {
        // Rest of your onCreate method here
        // Lấy chapterId từ Intent
        Intent intent = getIntent();
        String chapterId = intent.getStringExtra("chapterId");

        // Lấy reference của các thành phần giao diện từ layout
        TextView textViewChapterName = findViewById(R.id.textViewChapterName);
        RecyclerView recyclerViewImages = findViewById(R.id.recyclerViewImages);
        ImageButton btnPreviousChapter = findViewById(R.id.btnPreviousChapter);
        ImageButton btnNextChapter = findViewById(R.id.btnNextChapter);
        ImageButton btn_viewchapter = findViewById(R.id.btn_viewchapter);
        ImageButton btnShowComments = findViewById(R.id.btnShowComments);

        // Ve trang detail de chon chap
        btn_viewchapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChapterActivity.this, DetailActivity.class);
                intent.putExtra("toonId", toonId);
                startActivity(intent);
            }
        });
        btnShowComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị CommentFragment khi người dùng nhấn vào nút
                showCommentFragment();
            }
        });

        // Xử lý khi nhấn nút Quay lại chap trước
        btnPreviousChapter.setOnClickListener(view -> onPreviousChapterClick());

        // Xử lý khi nhấn nút Chuyển đến chap tiếp theo
        btnNextChapter.setOnClickListener(view -> onNextChapterClick());

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
                    currentChapterId = chapter.getChapterId();

                    // Hiển thị tên chương vào TextView
                    textViewChapterName.setText(chapterName);

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

    // Phương thức để hiển thị CommentFragment
    private void showCommentFragment() {
        // Tạo một instance mới của CommentFragment
        CommentFragment commentFragment = new CommentFragment();
        // Chuyển qua FragmentManager để bắt đầu một giao dịch Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Thay thế fragment hiện tại bằng CommentFragment
        transaction.replace(R.id.fragment_container, commentFragment);
        // Thêm transaction vào back stack để cho phép người dùng quay lại fragment trước đó (nếu cần)
        transaction.addToBackStack(null);
        // Kết thúc
        transaction.commit();
    }

    public void onPreviousChapterClick() {
        // Xử lý khi click vào nút Quay lại chap trước
        Intent intent = new Intent(ChapterActivity.this, ChapterActivity.class);
        intent.putExtra("toonId", toonId);

        // Lấy chapterId của chap trước đó
        DatabaseReference chaptersRef = FirebaseDatabase.getInstance().getReference().child("chapters").child(toonId);
        Query previousChapterQuery = chaptersRef.orderByKey().endAt(currentChapterId).limitToLast(2);
        previousChapterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String previousChapterId = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals(currentChapterId)) {
                        previousChapterId = snapshot.getKey();
                        break;
                    }
                }
                if (previousChapterId != null) {
                    intent.putExtra("chapterId", previousChapterId);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi không thể truy xuất dữ liệu chương từ Firebase
                Toast.makeText(ChapterActivity.this, "Failed to load chapter: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onNextChapterClick() {
        // Xử lý khi click vào nút Chuyển đến chap tiếp theo
        Intent intent = new Intent(ChapterActivity.this, ChapterActivity.class);
        intent.putExtra("toonId", toonId);

        // Lấy chapterId của chap tiếp theo
        DatabaseReference chaptersRef = FirebaseDatabase.getInstance().getReference().child("chapters").child(toonId);
        Query nextChapterQuery = chaptersRef.orderByKey().startAt(currentChapterId).limitToFirst(2);
        nextChapterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean firstIteration = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!firstIteration) {
                        String nextChapterId = snapshot.getKey();
                        intent.putExtra("chapterId", nextChapterId);
                        startActivity(intent);
                        break;
                    }
                    firstIteration = false;
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
