package com.my.greentoon.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

public class EditToonActivity extends AppCompatActivity {

    private EditText edtStoryName, edtStoryGenre, edtStoryDescription;
    private Button btnSaveChanges;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private Toon toon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_toon);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Toon");

        edtStoryName = findViewById(R.id.edtStoryName);
        edtStoryGenre = findViewById(R.id.edtStoryGenre);
        edtStoryDescription = findViewById(R.id.edtStoryDescription);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Retrieve Toon object from Intent
        toon = (Toon) getIntent().getSerializableExtra("toon");

        if (toon != null) {
            // Set existing data to the UI elements
            edtStoryName.setText(toon.getStoryName());
            edtStoryGenre.setText(toon.getStoryGenre());
            edtStoryDescription.setText(toon.getStoryDescription());
        }

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && toon != null) {
            String userId = user.getUid();
            String storyName = edtStoryName.getText().toString().trim();
            String storyGenre = edtStoryGenre.getText().toString().trim();
            String storyDescription = edtStoryDescription.getText().toString().trim();

            if (!storyName.isEmpty() && !storyGenre.isEmpty() && !storyDescription.isEmpty()) {
                // Tạo một đối tượng mới với các thông tin đã chỉnh sửa
                Toon updatedToon = new Toon(userId, storyName, storyGenre, storyDescription, toon.getStoryBookCover());

                // Lấy tham chiếu đến nút con cần cập nhật
                DatabaseReference toonRef = FirebaseDatabase.getInstance().getReference("Toon").child(userId).child(toon.getStoryName());

                // Cập nhật dữ liệu của nút con hiện tại
                toonRef.setValue(updatedToon)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditToonActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditToonActivity.this, "Failed to save changes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(EditToonActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
