package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.my.greentoon.R;

public class EditProfileActivity extends AppCompatActivity {
Button btUsername,btBack,btImg,btUpload,btEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btBack = findViewById(R.id.btBack);
        btUsername = findViewById(R.id.btUsername);
        btImg = findViewById(R.id.btImg);
        btUpload=findViewById(R.id.btUpload);
        btEdit=findViewById(R.id.btEdit);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, EditUsernameActivity.class);
                startActivity(intent);
            }
        });
        btImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, EditAvatarActivity.class);
                startActivity(intent);
            }
        });
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, UploadStoryActivity.class);
                startActivity(intent);
            }
        });
        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, ListStoriesActivity.class);
                startActivity(intent);
            }
        });
    }
}