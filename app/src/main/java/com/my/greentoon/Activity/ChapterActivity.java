package com.my.greentoon.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.my.greentoon.R;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        // Nhận thông tin chương từ Intent
        String chapterName = getIntent().getStringExtra("chapterName");
        List<String> imageUrls = getIntent().getStringArrayListExtra("imageUrls");

        // Hiển thị tên chương
        setTitle(chapterName);

        // Tạo LinearLayout để chứa các hình ảnh
        LinearLayout imageContainer = findViewById(R.id.imageContainer);

        // Hiển thị các hình ảnh trong LinearLayout
        for (String imageUrl : imageUrls) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(layoutParams);
            imageView.setAdjustViewBounds(true);
            Glide.with(this).load(imageUrl).into(imageView); // Sử dụng Glide để tải và hiển thị hình ảnh từ URL
            imageContainer.addView(imageView);
        }
    }
}
