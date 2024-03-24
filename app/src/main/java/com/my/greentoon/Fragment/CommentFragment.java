package com.my.greentoon.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.CommentAdapter;
import com.my.greentoon.Model.Comment;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference commentsRef;

    private EditText editTextComment;
    private Button btnPostComment;
    private RecyclerView recyclerViewComments;
    private List<Comment> commentsList;
    private CommentAdapter commentsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        // Khởi tạo các thành phần Firebase
        mAuth = FirebaseAuth.getInstance();
        commentsRef = FirebaseDatabase.getInstance().getReference().child("comments");

        // Khởi tạo các view
        editTextComment = view.findViewById(R.id.editTextComment);
        btnPostComment = view.findViewById(R.id.btnPostComment);
        recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách và adapter cho các bình luận
        commentsList = new ArrayList<>();
        commentsAdapter = new CommentAdapter(getContext(), commentsList);
        recyclerViewComments.setAdapter(commentsAdapter);

        // Đọc các bình luận từ Firebase Database
        readComments();

        // Xử lý khi người dùng nhấn nút gửi bình luận
        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        return view;
    }

    private void readComments() {
        // Đính kèm một bộ lắng nghe để đọc dữ liệu tại tham chiếu của các bình luận
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa danh sách bình luận hiện có
                commentsList.clear();
                // Duyệt qua mỗi bình luận trong dataSnapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Phân tích dữ liệu của bình luận và thêm vào danh sách commentsList
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentsList.add(comment);
                    }
                }
                // Thông báo cho adapter biết rằng tập dữ liệu đã thay đổi
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi gặp lỗi trong quá trình đọc từ cơ sở dữ liệu
                Toast.makeText(getContext(), "Không thể đọc bình luận: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment() {
        // Lấy ID của người dùng hiện tại
        String userId = mAuth.getCurrentUser().getUid();
        // Lấy nội dung bình luận từ EditText
        String commentText = editTextComment.getText().toString().trim();
        // Kiểm tra nếu nội dung bình luận không rỗng
        if (!commentText.isEmpty()) {
            // Tạo một đối tượng Comment mới
            Comment comment = new Comment(userId, commentText);
            // Đẩy bình luận mới lên Firebase database
            commentsRef.push().setValue(comment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Xóa nội dung trong EditText sau khi gửi bình luận thành công
                            editTextComment.setText("");
                            Toast.makeText(getContext(), "Đăng bình luận thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Không thể đăng bình luận", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
        }
    }
}
