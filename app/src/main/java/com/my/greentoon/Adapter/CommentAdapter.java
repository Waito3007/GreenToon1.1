package com.my.greentoon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.my.greentoon.Model.Comment;
import com.my.greentoon.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCommentText;
        private TextView textViewCommentUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCommentText = itemView.findViewById(R.id.textViewCommentText);
            textViewCommentUser = itemView.findViewById(R.id.textViewCommentUser);
        }

        public void bind(Comment comment) {
            textViewCommentText.setText(comment.getCommentText());
            textViewCommentUser.setText("User ID: " + comment.getUserId());
        }
    }
}
