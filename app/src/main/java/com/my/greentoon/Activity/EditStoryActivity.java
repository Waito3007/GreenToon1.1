package com.my.greentoon.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

public class EditStoryActivity extends AppCompatActivity {

    private ImageView imgStoryCover;
    private EditText edtStoryName, edtStoryGenre, edtStoryDescription;
    private Button btnSave, btnDelete;

    private DatabaseReference databaseReference;
    private Toon selectedToon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        databaseReference = FirebaseDatabase.getInstance().getReference("Toon");

        imgStoryCover = findViewById(R.id.imgStoryCover);
        edtStoryName = findViewById(R.id.edtStoryName);
        edtStoryGenre = findViewById(R.id.edtStoryGenre);
        edtStoryDescription = findViewById(R.id.edtStoryDescription);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Nhận dữ liệu từ Intent
        selectedToon = (Toon) getIntent().getSerializableExtra("selectedToon");

        // Hiển thị dữ liệu của truyện đang chọn lên giao diện
        displaySelectedToonData();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStory();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStory();
            }
        });
    }

    private void displaySelectedToonData() {
        if (selectedToon != null) {
            Glide.with(this).load(selectedToon.getStoryBookCover()).placeholder(R.drawable.sliderimg3).into(imgStoryCover);
            edtStoryName.setText(selectedToon.getStoryName());
            edtStoryGenre.setText(selectedToon.getStoryGenre());
            edtStoryDescription.setText(selectedToon.getStoryDescription());
        }
    }

    private void updateStory() {
        if (selectedToon != null) {
            String updatedName = edtStoryName.getText().toString().trim();
            String updatedGenre = edtStoryGenre.getText().toString().trim();
            String updatedDescription = edtStoryDescription.getText().toString().trim();

            // Cập nhật thông tin truyện trong cơ sở dữ liệu
            selectedToon.setStoryName(updatedName);
            selectedToon.setStoryGenre(updatedGenre);
            selectedToon.setStoryDescription(updatedDescription);

            databaseReference.child(selectedToon.getUserId()).setValue(selectedToon);

            Toast.makeText(EditStoryActivity.this, "Cập nhật truyện thành công", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void deleteStory() {
        if (selectedToon != null) {
            // Xóa truyện từ cơ sở dữ liệu
            databaseReference.child(selectedToon.getUserId()).removeValue();

            Toast.makeText(EditStoryActivity.this, "Xóa truyện thành công", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
