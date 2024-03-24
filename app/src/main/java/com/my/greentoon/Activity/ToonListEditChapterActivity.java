package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.ToonAdapter;
import com.my.greentoon.Fragment.SearchFragment;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class ToonListEditChapterActivity extends AppCompatActivity {

    private ListView listViewToons;
    private List<Toon> toonList;
    private DatabaseReference databaseReference;
    private Button btBack;
    private ImageButton btSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_list);
        listViewToons = findViewById(R.id.listViewToons);
        toonList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("toons");
        btBack = findViewById(R.id.btBack);
        btSearch = findViewById(R.id.btSearch);

        // Bổ sung sự kiện khi click vào nút btSearch
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một instance của SearchFragment
                SearchFragment searchFragment = new SearchFragment();

                // Bắt đầu một FragmentTransaction
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Thêm SearchFragment vào activity và thay thế fragment hiện tại (nếu có)
                transaction.replace(R.id.fragment_container, searchFragment);

                // Thực hiện FragmentTransaction
                transaction.commit();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toonList.clear();
                for (DataSnapshot toonSnapshot : dataSnapshot.getChildren()) {
                    Toon toon = toonSnapshot.getValue(Toon.class);
                    toonList.add(toon);
                }
                ToonAdapter adapter = new ToonAdapter(ToonListEditChapterActivity.this, R.layout.item_toon, toonList);
                listViewToons.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ToonListEditChapterActivity.this, "Failed to load toons: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện khi click vào một toon
        listViewToons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy toon được chọn từ danh sách
                Toon selectedToon = toonList.get(position);
                // Tạo một Intent để chuyển tới EditChapterActivity
                Intent intent = new Intent(ToonListEditChapterActivity.this, EditChapterActivity.class);
                // Truyền ID của toon được chọn qua Intent
                intent.putExtra("toonId", selectedToon.getToonId());
                // Khởi chạy EditChapterActivity với Intent đã tạo
                startActivity(intent);
            }
        });
        // Xử lý sự kiện khi click vào nút btBack
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trở về MainActivity
                Intent intent = new Intent(ToonListEditChapterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
