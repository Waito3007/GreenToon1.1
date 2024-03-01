package com.my.greentoon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.greentoon.Activity.DetailActivity;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.List;

public class TopToonAdapter extends RecyclerView.Adapter<TopToonAdapter.TopToonViewHolder> {
    private Context context;
    private List<Toon> toonList;

    public TopToonAdapter(Context context, List<Toon> toonList) {
        this.context = context;
        this.toonList = toonList;
    }

    @NonNull
    @Override
    public TopToonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_toon, parent, false);
        return new TopToonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopToonViewHolder holder, int position) {
        Toon toon = toonList.get(position);

        // Load ảnh bìa của truyện
        Glide.with(context).load(toon.getToonCover()).into(holder.imageViewToonCover);

        // Hiển thị tên và mô tả của truyện
        holder.textToonName.setText(toon.getToonName());
        holder.textToonDescription.setText(toon.getToonDes());

        // Xử lý sự kiện khi người dùng nhấn vào mục
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang DetailActivity và truyền toonId
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("toonId", toon.getToonId());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return toonList.size();
    }

    public class TopToonViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewToonCover;
        TextView textToonName, textToonDescription;

        public TopToonViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewToonCover = itemView.findViewById(R.id.image_top_toon_cover);
            textToonName = itemView.findViewById(R.id.text_top_toon_name);
            textToonDescription = itemView.findViewById(R.id.text_top_toon_description);
        }
    }
}
