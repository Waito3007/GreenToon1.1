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
        holder.textViewUserName.setText(currentUser.getNameUser());
        // Hiển thị thông tin của người đăng bài
        Glide.with(context).load(currentUser.getAvatarUser()).into(holder.imageViewUserAvatar);
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
