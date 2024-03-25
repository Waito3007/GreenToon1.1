package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

public class ToonListEditToonActivity extends AppCompatActivity {

    private ListView listViewToons;
    private List<Toon> toonList;
    private DatabaseReference databaseReference;
    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_list_edit_toon);

        listViewToons = findViewById(R.id.listViewToons);
        editTextSearch = findViewById(R.id.editTextSearch);
        toonList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("toons");

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editTextSearch.getText().toString().trim();
                searchToons(searchText);
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

                ToonAdapter adapter = new ToonAdapter(ToonListEditToonActivity.this, R.layout.item_toon, toonList);
                listViewToons.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        listViewToons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ToonListEditToonActivity.this, EditToonActivity.class);
                intent.putExtra("toonId", toonList.get(position).getToonId());
                startActivity(intent);
            }
        });
    }

    private void searchToons(String searchText) {
        List<Toon> filteredList = new ArrayList<>();
        for (Toon toon : toonList) {
            if (toon.getToonName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(toon);
            }
        }
        // Cập nhật danh sách truyện hiển thị trên ListView
        ToonAdapter adapter = new ToonAdapter(ToonListEditToonActivity.this, R.layout.item_toon, filteredList);
        listViewToons.setAdapter(adapter);
    }
}
