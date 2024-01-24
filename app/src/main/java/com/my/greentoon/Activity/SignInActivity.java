package com.my.greentoon.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.my.greentoon.R;

// SignInActivity.java
public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Tìm nút có id là btback
        Button btBack = findViewById(R.id.btback);

        // Thiết lập sự kiện click cho nút btBack
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại fragment_profile
                finish(); // Đóng SignInActivity và trở về fragment_profile
            }
        });

        // ... Các xử lý khác khi mở activiti_sign_in
    }
}
