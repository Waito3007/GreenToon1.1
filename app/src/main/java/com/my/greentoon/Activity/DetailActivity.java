package com.my.greentoon.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.ChapterAdapter;
import com.my.greentoon.Model.Chapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewToonCover;
    private TextView textViewToonName;
    private TextView textViewViewCount;

    private TextView textViewToonDes;
    private ListView listViewChapters;
    private List<Chapter> chapterList;
    private DatabaseReference databaseReference;
    Button btBack;
    ImageButton btnFollow;

    private boolean isToonFollowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Khởi tạo các button và ánh xạ từ layout
        btBack = findViewById(R.id.btBack);
        btnFollow = findViewById(R.id.btnFollow);
        imageViewToonCover = findViewById(R.id.imageViewToonCover);
        textViewToonName = findViewById(R.id.textViewToonName);
        textViewToonDes = findViewById(R.id.textViewToonDes);
        listViewChapters = findViewById(R.id.listViewChapters);
        String toonId = getIntent().getStringExtra("toonId");
        textViewViewCount = findViewById(R.id.textViewViewCount);
        chapterList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Xác định ID của người dùng hiện tại
        String currentUserId = getCurrentUserId();

        // Lấy dữ liệu của toon từ Firebase Database
        DatabaseReference toonRef = databaseReference.child("toons").child(toonId);

        // Kiểm tra xem truyện đã được theo dõi hay chưa
        DatabaseReference followRef = databaseReference.child("follows").child(currentUserId).child(toonId);
        followRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isToonFollowed = dataSnapshot.exists();
                // Cập nhật trạng thái của nút theo dõi dựa trên kết quả kiểm tra
                updateFollowButtonState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to check follow status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        toonRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toon toon = dataSnapshot.getValue(Toon.class);
                if (toon != null) {
                    // Hiển thị thông tin của toon
                    Picasso.get().load(toon.getToonCover()).into(imageViewToonCover);
                    textViewToonName.setText(toon.getToonName());
                    textViewToonDes.setText(toon.getToonDes());

                    // Hiển thị số lượt xem
                    textViewViewCount.setText("Số lượt xem: " + toon.getViewCount());
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to load toon details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Lấy danh sách chapter thuộc toon từ Firebase Database
        DatabaseReference chaptersRef = databaseReference.child("chapters").child(toonId);
        chaptersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterList.clear();
                for (DataSnapshot chapterSnapshot : dataSnapshot.getChildren()) {
                    Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        chapterList.add(chapter);
                    }
                }

                ChapterAdapter adapter = new ChapterAdapter(DetailActivity.this, R.layout.item_chapter, chapterList);
                listViewChapters.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to load chapters: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện khi người dùng chọn một chapter để xem
        listViewChapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Chapter selectedChapter = chapterList.get(position);
                List<String> imageUrls = selectedChapter.getListImgChapter();
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    // Tăng viewCount của toon lên 1
                    DatabaseReference toonRef = databaseReference.child("toons").child(toonId);
                    toonRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Toon toon = dataSnapshot.getValue(Toon.class);
                            if (toon != null) {
                                int currentViewCount = toon.getViewCount();
                                int updatedViewCount = currentViewCount + 1;
                                toonRef.child("viewCount").setValue(updatedViewCount); // Cập nhật vào Firebase
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(DetailActivity.this, "Failed to update view count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Chuyển đến ChapterActivity và truyền danh sách URL hình ảnh của chapter
                    Intent intent = new Intent(DetailActivity.this, ChapterActivity.class);
                    intent.putExtra("chapterId", selectedChapter.getChapterId()); // Truyền ID của chapter
                    intent.putExtra("toonId", selectedChapter.getToonId()); // Truyền ID của toon

                    startActivity(intent);
                } else {
                    Toast.makeText(DetailActivity.this, "Danh sách ảnh không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference followRef = databaseReference.child("follows").child(getCurrentUserId()).child(toonId);
                followRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Truyện đã được theo dõi, nên bỏ theo dõi
                            unfollowToon(toonId);
                            btnFollow.setImageResource(R.drawable.bookmark_white);
                            Toast.makeText(DetailActivity.this, "Unfollowed this toon", Toast.LENGTH_SHORT).show();
                        } else {
                            // Truyện chưa được theo dõi, nên theo dõi
                            followToon(toonId);
                            btnFollow.setImageResource(R.drawable.bookmark_black);
                            Toast.makeText(DetailActivity.this, "Followed this toon", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void unfollowToon(String toonId) {
                        DatabaseReference followRef = databaseReference.child("follows").child(getCurrentUserId()).child(toonId);
                        followRef.removeValue();
                    }

                    private void followToon(String toonId) {
                        DatabaseReference followRef = databaseReference.child("follows").child(getCurrentUserId()).child(toonId);
                        followRef.setValue(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DetailActivity.this, "Failed to check follow status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    // Thêm phương thức updateFollowButtonState để cập nhật trạng thái của nút theo dõi
    private void updateFollowButtonState() {
        if (isToonFollowed) {
            btnFollow.setImageResource(R.drawable.bookmark_black); // Thay đổi hình ảnh cho trạng thái "Follow"
        } else {
            btnFollow.setImageResource(R.drawable.bookmark_white); // Thay đổi hình ảnh cho trạng thái "Unfollow"
        }
    }

    private String getCurrentUserId() {
        return "current_user_id";
    }
}
