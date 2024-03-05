package com.my.greentoon.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TopToonAdapter extends RecyclerView.Adapter<TopToonAdapter.TopToonViewHolder> {
    private Context context;
    private List<Toon> toonList;
    private int currentPosition = 0;
    private Timer timer;

    public TopToonAdapter(Context context, List<Toon> toonList) {
        this.context = context;
        this.toonList = toonList;
        // Bắt đầu chuyển đổi tự động khi Adapter được tạo
        startAutoScroll();
    }

    // Hàm này bắt đầu chuyển đổi tự động
    private void startAutoScroll() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPosition == getItemCount() - 1) {
                            currentPosition = 0;
                        } else {
                            currentPosition++;
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        }, 1800, 1800); // Chuyển slide sau mỗi 1.8s
    }

    // Hàm này dừng chuyển đổi tự động
    private void stopAutoScroll() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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

    // Tạm dừng chuyển đổi tự động khi RecyclerView bị hủy
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        stopAutoScroll();
    }
}
