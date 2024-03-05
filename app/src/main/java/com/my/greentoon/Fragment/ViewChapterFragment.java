package com.my.greentoon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Activity.ChapterActivity;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class ViewChapterFragment extends DialogFragment {

    private List<Integer> numChapterList;
    private String toonId; // Thêm biến toonId

    public static ViewChapterFragment newInstance(List<Integer> numChapterList, String toonId) {
        ViewChapterFragment fragment = new ViewChapterFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList("numChapterList", (ArrayList<Integer>) numChapterList);
        args.putString("toonId", toonId); // Gán giá trị toonId
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numChapterList = getArguments().getIntegerArrayList("numChapterList");
            toonId = getArguments().getString("toonId"); // Lấy giá trị toonId
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_chapter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (numChapterList != null) {
            updateUI();
        } else {
            // Nếu danh sách số chap là null, ta cần tải dữ liệu từ Firebase
            loadChaptersFromFirebase();
        }
    }

    private void loadChaptersFromFirebase() {
        // Lấy reference của chapters từ Firebase Database
        DatabaseReference chaptersRef = FirebaseDatabase.getInstance().getReference().child("chapters").child(toonId);
        chaptersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numChapterList = new ArrayList<>();
                for (DataSnapshot chapterSnapshot : dataSnapshot.getChildren()) {
                    // Lấy số chapter và thêm vào danh sách
                    Integer numChapter = chapterSnapshot.child("numChapter").getValue(Integer.class);
                    if (numChapter != null) {
                        numChapterList.add(numChapter);
                    }
                }
                // Sau khi tải dữ liệu xong, cập nhật giao diện
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    private void updateUI() {
        // Cập nhật giao diện sau khi tải dữ liệu từ Firebase xong
        if (getView() != null && getContext() != null) {
            LinearLayout linearLayoutChapterList = getView().findViewById(R.id.linearLayoutChapterList);
            TextView countChapter = getView().findViewById(R.id.countchapter);
            TextView chapterView = getView().findViewById(R.id.chapterview);

            // Xóa tất cả các view cũ trước khi thêm mới
            linearLayoutChapterList.removeAllViews();

            // Tính tổng số chương
            int totalChapter = numChapterList.size();
            countChapter.setText("Tổng số chương: " + totalChapter);

            // Hiển thị từng chương
            for (final Integer numChapter : numChapterList) {
                TextView textViewChapter = new TextView(getContext());
                textViewChapter.setText("Chap " + numChapter);
                textViewChapter.setTextSize(28); // Đặt kích thước font là 30dp
                textViewChapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Mở ChapterActivity với chương có số chap là numChapter
                        openChapterActivity(numChapter);
                    }
                });
                linearLayoutChapterList.addView(textViewChapter);
            }
        }
    }

    private void openChapterActivity(int selectedChapterNum) {
        // Tạo tham chiếu đến Chapter cụ thể trong Firebase theo toonId và số chap
        DatabaseReference chapterRef = FirebaseDatabase.getInstance().getReference()
                .child("chapters")
                .child(toonId)
                .orderByChild("numChapter")
                .equalTo(selectedChapterNum)
                .getRef();

        // Thực hiện truy vấn
        chapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra xem có Chapter tương ứng không
                if (dataSnapshot.exists()) {
                    // Lặp qua tất cả các Chapter (tuy nhiên chỉ có một Chapter với số chap duy nhất)
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Lấy id của Chapter
                        String chapterId = snapshot.getKey();

                        // Chuyển tới ChapterActivity khi click vào một chương
                        Intent intent = new Intent(getContext(), ChapterActivity.class);
                        intent.putExtra("toonId", toonId);
                        intent.putExtra("chapterId", chapterId);
                        startActivity(intent);
                    }
                } else {
                    // Xử lý khi không tìm thấy Chapter tương ứng
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Lấy kích thước màn hình
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Tính kích thước cho dialog fragment
        int dialogWidth = screenWidth;
        int dialogHeight = (1 * screenHeight) / 3;

        // Lấy dialog window
        Window window = getDialog().getWindow();
        if (window != null) {
            // Đặt kích thước cho dialog window
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.TOP | Gravity.START; // Đặt vị trí từ trái qua phải, từ trên xuống
            params.x = 0; // Đặt vị trí từ trái sang phải tại vị trí 0
            params.y = 55; // Đặt vị trí từ trên xuống dưới
            params.width = dialogWidth; // Đặt chiều rộng
            params.height = dialogHeight; // Đặt chiều cao
            window.setAttributes(params);
        }
    }
}
