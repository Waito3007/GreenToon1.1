package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.my.greentoon.R;

public class StatusActivity extends AppCompatActivity {

    private Button btnLike, btnComment;
    private TextView textViewStatusText;
    private ImageView imageViewStatusContent;

    private boolean isLiked = false; // Trạng thái của lượt thích

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // Ánh xạ các thành phần giao diện
        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        textViewStatusText = findViewById(R.id.textViewStatusText);
        imageViewStatusContent = findViewById(R.id.imageViewStatusContent);

        // Xử lý sự kiện khi người dùng nhấn nút "Thích"
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLiked) {
                    // Nếu chưa thích, thêm một lượt thích
                    // Đồng thời cập nhật giao diện người dùng
                    isLiked = true;
                    btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_black, 0, 0, 0);
                } else {
                    // Nếu đã thích, hủy lượt thích
                    // Đồng thời cập nhật giao diện người dùng
                    isLiked = false;
                    btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white, 0, 0, 0);
                }
            }
        });

        // Xử lý sự kiện khi người dùng nhấn nút "Bình luận"
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển hướng người dùng đến màn hình bình luận
                // Bạn có thể sử dụng Intent để chuyển hướng đến một activity hoặc fragment khác
            }
        });
    }
}
