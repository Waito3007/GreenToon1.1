package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private LinearLayout linearButtonFollow;
    private Button btBack,btHome,btNewchap,btChap1;
    private ImageButton btnFollow;
    private boolean isToonFollowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        btBack = findViewById(R.id.btBack);
        btChap1 = findViewById(R.id.btChap1);
        btNewchap = findViewById(R.id.btNewchap);
        btnFollow = findViewById(R.id.btnFollow);
        linearButtonFollow = findViewById(R.id.linearButtonFollow);
        imageViewToonCover = findViewById(R.id.imageViewToonCover);
        textViewToonName = findViewById(R.id.textViewToonName);
        textViewToonDes = findViewById(R.id.textViewToonDes);
        listViewChapters = findViewById(R.id.listViewChapters);
        textViewViewCount = findViewById(R.id.textViewViewCount);
        btHome = findViewById(R.id.btHome);
        chapterList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        String toonId = getIntent().getStringExtra("toonId");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference followRef = databaseReference.child("follows").child(currentUserId).child(toonId);
            followRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    isToonFollowed = dataSnapshot.exists();
                    updateFollowButtonState();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DetailActivity.this, "Failed to check follow status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        DatabaseReference toonRef = databaseReference.child("toons").child(toonId);
        toonRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toon toon = dataSnapshot.getValue(Toon.class);
                if (toon != null) {
                    Picasso.get().load(toon.getToonCover()).into(imageViewToonCover);
                    textViewToonName.setText(toon.getToonName());
                    textViewToonDes.setText(toon.getToonDes());
                    textViewViewCount.setText("Số lượt xem: " + toon.getViewCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to load toon details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        linearButtonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    Toast.makeText(DetailActivity.this, "Bạn cần đăng nhập để theo dõi truyện", Toast.LENGTH_SHORT).show();
                } else {
                    String currentUserId = currentUser.getUid();
                    DatabaseReference followRef = databaseReference.child("follows").child(currentUserId).child(toonId);
                    followRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                unfollowToon(toonId, currentUserId);
                                btnFollow.setImageResource(R.drawable.bookmark_white);
                                Toast.makeText(DetailActivity.this, "Đã bỏ theo dõi truyện", Toast.LENGTH_SHORT).show();
                            } else {
                                followToon(toonId, currentUserId);
                                btnFollow.setImageResource(R.drawable.bookmark_black);
                                Toast.makeText(DetailActivity.this, "Đã theo dõi truyện", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(DetailActivity.this, "Failed to check follow status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        btNewchap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến ChapterActivity của chap mới nhất
                if (!chapterList.isEmpty()) {
                    Chapter latestChapter = chapterList.get(chapterList.size() - 1);
                    moveToChapterActivity(latestChapter);
                } else {
                    Toast.makeText(DetailActivity.this, "Không có chap nào", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btChap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến ChapterActivity của chap đầu tiên
                if (!chapterList.isEmpty()) {
                    Chapter firstChapter = chapterList.get(0);
                    moveToChapterActivity(firstChapter);
                } else {
                    Toast.makeText(DetailActivity.this, "Không có chap nào", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateFollowButtonState() {
        if (isToonFollowed) {
            btnFollow.setImageResource(R.drawable.bookmark_black);
        } else {
            btnFollow.setImageResource(R.drawable.bookmark_white);
        }
    }

    private void followToon(String toonId, String currentUserId) {
        DatabaseReference followRef = databaseReference.child("follows").child(currentUserId).child(toonId);
        followRef.setValue(true);
    }

    private void unfollowToon(String toonId, String currentUserId) {
        DatabaseReference followRef = databaseReference.child("follows").child(currentUserId).child(toonId);
        followRef.removeValue();
    }
    private void moveToChapterActivity(Chapter chapter) {
        Intent intent = new Intent(DetailActivity.this, ChapterActivity.class);
        intent.putExtra("chapterId", chapter.getChapterId());
        intent.putExtra("toonId", chapter.getToonId());
        startActivity(intent);
    }

}