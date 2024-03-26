package com.my.greentoon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private User currentUser;

    public StatusAdapter(Context context, List<Status> statusList, User currentUser) {
        this.context = context;
        this.statusList = statusList;
        this.currentUser = currentUser;
    }

    // Phương thức setter để thiết lập commentClickListener
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewUserAvatar, imageViewStatusContent;
        TextView textViewUserName;
        TextView textViewStatusText;
        Button btnLike, btnComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUserAvatar = itemView.findViewById(R.id.imageViewUserAvatar);
            imageViewStatusContent = itemView.findViewById(R.id.imageViewStatusContent);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewStatusText = itemView.findViewById(R.id.textViewStatusText);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);

            btnLike.setOnClickListener(this);
            btnComment.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                switch (v.getId()) {
                    case R.id.btnLike:
                        likeStatus(position);
                        break;
                    case R.id.btnComment:
                        // Xử lý khi nút bình luận được nhấn
                        break;
                }
            }
        }

        private void likeStatus(int position) {
            Status status = statusList.get(position);
            String statusId = status.getStatusId();
            String userId = currentUser.getUserId();

            DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("likes").child(statusId).child(userId);

            likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        likesRef.removeValue();
                        // Cập nhật trạng thái của nút thích
                        btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white, 0, 0, 0);
                        btnLike.setTextColor(context.getResources().getColor(R.color.black));
                    } else {
                        likesRef.setValue(true);
                        // Cập nhật trạng thái của nút thích
                        btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_black, 0, 0, 0);
                        btnLike.setTextColor(context.getResources().getColor(R.color.black));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý khi truy vấn bị hủy bỏ
                }
            });
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
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
                                .placeholder(R.drawable.ic_default_avatar)
                                .error(R.drawable.ic_default_avatar)
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

        // Kiểm tra xem người dùng đã thích status này chưa
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("likes").child(status.getStatusId()).child(currentUser.getUserId());
        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Người dùng đã thích status, cập nhật trạng thái của nút thích
                    holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_black, 0, 0, 0);
                    holder.btnLike.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    // Người dùng chưa thích status, cập nhật trạng thái của nút thích
                    holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white, 0, 0, 0);
                    holder.btnLike.setTextColor(context.getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi truy vấn bị hủy bỏ
            }
        });
    }


    @Override
    public int getItemCount() {
        return statusList.size();
    }
}