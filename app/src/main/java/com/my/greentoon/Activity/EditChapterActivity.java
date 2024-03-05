package com.my.greentoon.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Model.Chapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class EditChapterActivity extends AppCompatActivity {

    private Spinner spinnerToonList;
    private Spinner spinnerChapterList;
    private EditText editTextChapterName;
    private EditText editTextChapterTitle;
    private EditText editTextNumChapter;
    private Button btnEditChapter;
    private Button btnDeleteChapter;

    private DatabaseReference chaptersRef;
    private List<Toon> toonList;
    private List<Chapter> chapterList;
    private ArrayAdapter<Chapter> chapterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);

        spinnerToonList = findViewById(R.id.spinnerToonList);
        spinnerChapterList = findViewById(R.id.spinnerChapterList);
        editTextChapterName = findViewById(R.id.editTextChapterName);
        editTextChapterTitle = findViewById(R.id.editTextChapterTitle);
        editTextNumChapter = findViewById(R.id.editTextNumChapter);
        btnEditChapter = findViewById(R.id.btnEditChapter);
        btnDeleteChapter = findViewById(R.id.btnDeleteChapter);

        chaptersRef = FirebaseDatabase.getInstance().getReference("chapters");
        toonList = new ArrayList<>();
        chapterList = new ArrayList<>();

        ArrayAdapter<Toon> toonAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, toonList);
        toonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerToonList.setAdapter(toonAdapter);

        chapterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chapterList);
        chapterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChapterList.setAdapter(chapterAdapter);

        spinnerToonList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toon selectedToon = (Toon) parent.getItemAtPosition(position);
                if (selectedToon != null) {
                    loadChapters(selectedToon.getToonId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerChapterList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Chapter selectedChapter = (Chapter) parent.getItemAtPosition(position);
                if (selectedChapter != null) {
                    displayChapterInfo(selectedChapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnEditChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editChapter();
            }
        });

        btnDeleteChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChapter();
            }
        });

        loadToons();
    }

    private void loadToons() {
        DatabaseReference toonsRef = FirebaseDatabase.getInstance().getReference("toons");
        toonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Toon toon = snapshot.getValue(Toon.class);
                    if (toon != null) {
                        toonList.add(toon);
                    }
                }
                ArrayAdapter<Toon> adapter = (ArrayAdapter<Toon>) spinnerToonList.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditChapterActivity.this, "Failed to load toons: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChapters(String toonId) {
        DatabaseReference toonChapterRef = chaptersRef.child(toonId);
        toonChapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chapter chapter = snapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        chapterList.add(chapter);
                    }
                }
                chapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditChapterActivity.this, "Failed to load chapters: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayChapterInfo(Chapter chapter) {
        editTextChapterName.setText(chapter.getChapterName());
        editTextChapterTitle.setText(chapter.getChapterTitle());
        editTextNumChapter.setText(String.valueOf(chapter.getNumChapter()));
    }

    private void editChapter() {
        Chapter selectedChapter = (Chapter) spinnerChapterList.getSelectedItem();
        if (selectedChapter == null) {
            Toast.makeText(this, "Please select a chapter", Toast.LENGTH_SHORT).show();
            return;
        }

        String chapterName = editTextChapterName.getText().toString().trim();
        String chapterTitle = editTextChapterTitle.getText().toString().trim();
        int numChapter = Integer.parseInt(editTextNumChapter.getText().toString());

        // Update chapter information in the database
        DatabaseReference chapterRef = chaptersRef.child(selectedChapter.getToonId()).child(selectedChapter.getChapterId());
        chapterRef.child("chapterName").setValue(chapterName);
        chapterRef.child("chapterTitle").setValue(chapterTitle);
        chapterRef.child("numChapter").setValue(numChapter);

        Toast.makeText(this, "Đã Chỉnh Sửa Chapter đã chọn", Toast.LENGTH_SHORT).show();
        loadChapters(selectedChapter.getToonId());

        // Finish the current activity and start a new instance
        finish();
        startActivity(getIntent());
    }

    private void deleteChapter() {
        Chapter selectedChapter = (Chapter) spinnerChapterList.getSelectedItem();
        if (selectedChapter == null) {
            Toast.makeText(this, "Hãy chọn chap", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove chapter from the database
        DatabaseReference chapterRef = chaptersRef.child(selectedChapter.getToonId()).child(selectedChapter.getChapterId());
        chapterRef.removeValue();

        // Show toast message for successful deletion
        Toast.makeText(this, "Xóa Chap Thành Công", Toast.LENGTH_SHORT).show();

        // Reload chapters after deleting
        loadChapters(selectedChapter.getToonId());

        // Finish the current activity and start a new instance
        finish();
        startActivity(getIntent());
    }

}
