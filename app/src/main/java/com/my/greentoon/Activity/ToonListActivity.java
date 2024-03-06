package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.ToonAdapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class ToonListActivity extends AppCompatActivity {

    private ListView listViewToons;
    private List<Toon> toonList;
    private DatabaseReference databaseReference;
    Button btBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_list);

        listViewToons = findViewById(R.id.listViewToons);
        toonList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("toons");
        btBack = findViewById(R.id.btBack);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toonList.clear();
                for (DataSnapshot toonSnapshot : dataSnapshot.getChildren()) {
                    Toon toon = toonSnapshot.getValue(Toon.class);
                    toonList.add(toon);
                }
                ToonAdapter adapter = new ToonAdapter(ToonListActivity.this, R.layout.item_toon, toonList);
                listViewToons.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ToonListActivity.this, "Failed to load toons: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToonListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Xử lý sự kiện khi click vào một toon
        listViewToons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy thông tin của toon được chọn
                Toon selectedToon = toonList.get(position);

                // Chuyển sang DetailActivity và truyền toonId của toon được chọn
                Intent intent = new Intent(ToonListActivity.this, DetailActivity.class);
                intent.putExtra("toonId", selectedToon.getToonId());
                startActivity(intent);
            }
        });

    }
}
