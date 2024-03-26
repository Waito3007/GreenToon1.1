package com.my.greentoon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Model.Status;
import com.my.greentoon.Model.User;
import com.my.greentoon.R;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private Context context;
    private List<Status> statusList;
    private User currentUser; // Thêm currentUser vào Adapter

    public StatusAdapter(Context context, List<Status> statusList, User currentUser) {
        this.context = context;
        this.statusList = statusList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Status status = statusList.get(position);
        // Hiển thị thông tin của status
        holder.textViewStatusText.setText(status.getStatusTitle());
        // Lấy userId của người đăng bài từ status
        String userId = status.getUserId();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Hiển thị thông tin của người đăng bài
                        holder.textViewUserName.setText(user.getNameUser());
                        // Load hình ảnh và áp dụng circleCrop để tạo hình tròn
                        Glide.with(context)
                                .load(user.getAvatarUser())
                                .circleCrop()
                                .placeholder(R.drawable.ic_default_avatar) // Hình ảnh mặc định khi đang tải
                                .error(R.drawable.ic_default_avatar) // Hình ảnh khi có lỗi
                                .into(holder.imageViewUserAvatar);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi truy vấn bị hủy bỏ
            }
        });

        // Hiển thị nội dung của status
        Glide.with(context).load(status.getStatusContent()).into(holder.imageViewStatusContent);
    }


    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewUserAvatar,imageViewStatusContent;
        TextView textViewUserName;
        TextView textViewStatusText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUserAvatar = itemView.findViewById(R.id.imageViewUserAvatar);
            imageViewStatusContent = itemView.findViewById(R.id.imageViewStatusContent);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewStatusText = itemView.findViewById(R.id.textViewStatusText);
        }
    }
}
