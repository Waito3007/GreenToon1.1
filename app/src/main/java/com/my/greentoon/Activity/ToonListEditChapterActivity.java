package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class ToonListEditChapterActivity extends AppCompatActivity {

    private ListView listViewToons;
    private List<Toon> toonList;
    private List<String> allToonNames;
    private DatabaseReference databaseReference;
    private Button btBack;
    private AutoCompleteTextView autoCompleteTextViewSearch;
    private ToonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_list_edit_chapter);

        listViewToons = findViewById(R.id.listViewToons);
        autoCompleteTextViewSearch = findViewById(R.id.autoCompleteTextViewSearch);
        btBack = findViewById(R.id.btBack);
        toonList = new ArrayList<>();
        allToonNames = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("toons");

        setupAutoCompleteTextView();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toonList.clear();
                allToonNames.clear();
                for (DataSnapshot toonSnapshot : dataSnapshot.getChildren()) {
                    Toon toon = toonSnapshot.getValue(Toon.class);
                    toonList.add(toon);
                    allToonNames.add(toon.getToonName());
                }
                adapter = new ToonAdapter(ToonListEditChapterActivity.this, R.layout.item_toon, toonList);
                listViewToons.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ToonListEditChapterActivity.this, "Failed to load toons: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        listViewToons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toon selectedToon = toonList.get(position);
                Intent intent = new Intent(ToonListEditChapterActivity.this, EditChapterActivity.class);
                intent.putExtra("toonId", selectedToon.getToonId());
                startActivity(intent);
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupAutoCompleteTextView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, allToonNames);
        autoCompleteTextViewSearch.setAdapter(adapter);

        autoCompleteTextViewSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchTerm = s.toString();
                searchToons(searchTerm);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchToons(final String searchTerm) {
        final List<Toon> searchResults = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResults.clear();
                for (DataSnapshot toonSnapshot : dataSnapshot.getChildren()) {
                    Toon toon = toonSnapshot.getValue(Toon.class);
                    if (toon != null && toon.getToonName() != null && toon.getToonName().toLowerCase().contains(searchTerm.toLowerCase())) {
                        searchResults.add(toon);
                    }
                }
                // Cập nhật lại danh sách toonList với kết quả tìm kiếm
                toonList.clear();
                toonList.addAll(searchResults);

                adapter = new ToonAdapter(ToonListEditChapterActivity.this, R.layout.item_toon, searchResults);
                listViewToons.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ToonListEditChapterActivity.this, "Failed to search: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
