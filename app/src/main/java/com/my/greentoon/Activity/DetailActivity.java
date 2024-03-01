package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    private TextView textViewToonDes;
    private ListView listViewChapters;
    private List<Chapter> chapterList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageViewToonCover = findViewById(R.id.imageViewToonCover);
        textViewToonName = findViewById(R.id.textViewToonName);
        textViewToonDes = findViewById(R.id.textViewToonDes);
        listViewChapters = findViewById(R.id.listViewChapters);

        String toonId = getIntent().getStringExtra("toonId");

        chapterList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference toonRef = databaseReference.child("toons").child(toonId);
        toonRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toon toon = dataSnapshot.getValue(Toon.class);
                if (toon != null) {
                    Picasso.get().load(toon.getToonCover()).into(imageViewToonCover);
                    textViewToonName.setText(toon.getToonName());
                    textViewToonDes.setText(toon.getToonDes());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to load toon details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                Chapter selectedChapter = chapterList.get(position);
                List<String> imageUrls = selectedChapter.getListImgChapter(); // Lấy danh sách URL của ảnh

                if (imageUrls != null && !imageUrls.isEmpty()) {
                    // Chuyển sang ChapterActivity và truyền danh sách URL của các ảnh
                    Intent intent = new Intent(DetailActivity.this, ChapterActivity.class);
                    intent.putStringArrayListExtra("imageUrls", (ArrayList<String>) imageUrls);
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailActivity.this, "Danh sách ảnh không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
