package com.my.greentoon.Adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.List;

public class TopToonAdapter extends RecyclerView.Adapter<TopToonAdapter.TopToonViewHolder> {
    private Context context;
    private List<Toon> toonList;
    private OnItemClickListener listener;

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
        int currentItem = position % getItemCount();
        int prevItem = (position - 1 + getItemCount()) % getItemCount();
        int nextItem = (position + 1) % getItemCount();

        Toon currentToon = toonList.get(currentItem);
        Toon prevToon = toonList.get(prevItem);
        Toon nextToon = toonList.get(nextItem);

        // Bo góc ảnh và hiển thị ảnh bằng Glide
        loadRoundedImage(currentToon.getToonCover(), holder.imageTopToonCover);
        loadRoundedImage(prevToon.getToonCover(), holder.imagePrevToonCover);
        loadRoundedImage(nextToon.getToonCover(), holder.imageNextToonCover);

        holder.textToonName.setText(currentToon.getToonName());
        holder.textToonDescription.setText(currentToon.getToonDes());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(currentToon);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return toonList.size();
    }

    public class TopToonViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePrevToonCover, imageTopToonCover, imageNextToonCover;
        TextView textToonName, textToonDescription;

        public TopToonViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePrevToonCover = itemView.findViewById(R.id.image_prev_toon_cover);
            imageTopToonCover = itemView.findViewById(R.id.image_top_toon_cover);
            imageNextToonCover = itemView.findViewById(R.id.image_next_toon_cover);
            textToonName = itemView.findViewById(R.id.text_top_toon_name);
            textToonDescription = itemView.findViewById(R.id.text_top_toon_description);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Toon toon);
    }

    // Phương thức này sử dụng Glide để tải ảnh và bo góc cho ImageView
    private void loadRoundedImage(String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        // Bo góc ảnh
                        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), ((BitmapDrawable) resource).getBitmap());
                        roundedDrawable.setCornerRadius(16); // Đặt bán kính bo góc
                        imageView.setImageDrawable(roundedDrawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Không cần xử lý
                    }
                });
    }

}
