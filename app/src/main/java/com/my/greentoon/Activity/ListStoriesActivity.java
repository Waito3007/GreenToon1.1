package com.my.greentoon.Activity;// ListStoriesActivity.java

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.CustomListAdapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class ListStoriesActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView listView;
    private List<Toon> toonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stories);

        databaseReference = FirebaseDatabase.getInstance().getReference("Toon");
        listView = findViewById(R.id.listView);
        toonList = new ArrayList<>();

        // Lắng nghe sự kiện thay đổi trong database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                toonList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Toon toon = dataSnapshot.getValue(Toon.class);
                    if (toon != null) {
                        toonList.add(toon);
                    }
                }

                // Cập nhật danh sách truyện trên ListView
                updateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });

        // Xử lý sự kiện khi một mục trên ListView được chọn
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toon selectedToon = toonList.get(position);

                // Chuyển đến trang chỉnh sửa truyện
                Intent intent = new Intent(ListStoriesActivity.this, EditStoryActivity.class);
                intent.putExtra("selectedToon", selectedToon);
                startActivity(intent);
            }
        });
    }

    private void updateListView() {
        // Cập nhật danh sách truyện trên ListView sử dụng custom adapter
        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.list_item_story, toonList);
        listView.setAdapter(adapter);
    }
}
