package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Model.User;
import com.my.greentoon.R;

public class AdminPageActivity extends AppCompatActivity {
    Button btUsername,btBack,btImg,btUpload,btEdit,btEmail,btUploadChap,btEditUser,btEditChapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        // Khởi tạo các button và ánh xạ từ layout
        btBack = findViewById(R.id.btBack);
        btUsername = findViewById(R.id.btUsername);
        btImg = findViewById(R.id.btImg);
        btUpload = findViewById(R.id.btUpload);
        btEdit = findViewById(R.id.btEdit);
        btUploadChap = findViewById(R.id.btUploadChap);
        btEditUser = findViewById(R.id.btEditUser);
        btEditChapter = findViewById(R.id.btEditChapter);

        // Lấy tham chiếu đến node "users" trên Firebase Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {


            String userId = currentUser.getUid();
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.isAdmin()) {
                            // Nếu người dùng là Admin, hiển thị các button
                            btUpload.setVisibility(View.VISIBLE);
                            btEdit.setVisibility(View.VISIBLE);
                            btUploadChap.setVisibility(View.VISIBLE);
                            btEditUser.setVisibility(View.VISIBLE);
                            btEditChapter.setVisibility(View.VISIBLE);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu có
                }
            });
        }
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPageActivity.this, UploadToonActivity.class);
                startActivity(intent);
            }
        });
        btUploadChap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPageActivity.this, AddChapterActivity.class);
                startActivity(intent);
            }
        });
        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPageActivity.this,  ToonListEditActivity.class);
                startActivity(intent);
            }
        });
        btEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPageActivity.this,  UserListActivity.class);
                startActivity(intent);
            }
        });btEditChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPageActivity.this,  EditChapterActivity.class);
                startActivity(intent);
            }
        });
    }

}