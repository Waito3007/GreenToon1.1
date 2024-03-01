package com.my.greentoon.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.my.greentoon.Model.User;
import com.my.greentoon.R;
public class EditUsernameActivity extends AppCompatActivity {

    private EditText edUsername;
    private Button btSave,btBack;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_username);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        edUsername = findViewById(R.id.edUsername);
        btSave = findViewById(R.id.btSave);
        btBack = findViewById(R.id.btBack);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsername();
                //back ve trang trc

            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadUsername();
    }

    private void loadUsername() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Lấy thông tin người dùng từ Firebase Realtime Database
            databaseReference.child(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User userProfile = task.getResult().getValue(User.class);
                    if (userProfile != null) {
                        // Hiển thị tên người dùng hiện tại (nameUser)
                        edUsername.setText(userProfile.getNameUser());
                    }
                } else {
                    Toast.makeText(EditUsernameActivity.this, "Error loading username", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveUsername() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String newUsername = edUsername.getText().toString().trim();

            if (TextUtils.isEmpty(newUsername)) {
                Toast.makeText(EditUsernameActivity.this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật tên người dùng vào Firebase Realtime Database
            databaseReference.child(userId).child("nameUser").setValue(newUsername);

            Toast.makeText(EditUsernameActivity.this, "Cập nhật tên người dùng thành công", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}