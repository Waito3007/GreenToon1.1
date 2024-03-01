package com.my.greentoon.Activity;

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
import com.my.greentoon.Adapter.ToonAdapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class ToonListEditActivity extends AppCompatActivity {

    private ListView listViewToons;
    private List<Toon> toonList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_list_edit);

        listViewToons = findViewById(R.id.listViewToons);
        toonList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("toons");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toonList.clear();
                for (DataSnapshot toonSnapshot : dataSnapshot.getChildren()) {
                    Toon toon = toonSnapshot.getValue(Toon.class);
                    toonList.add(toon);
                }

                ToonAdapter adapter = new ToonAdapter(ToonListEditActivity.this, R.layout.item_toon, toonList);
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
                Intent intent = new Intent(ToonListEditActivity.this, EditToonActivity.class);
                intent.putExtra("toonId", toonList.get(position).getToonId());
                startActivity(intent);
            }
        });
    }
}
