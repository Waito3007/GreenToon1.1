package com.my.greentoon.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.my.greentoon.Model.ImageItem;
import com.my.greentoon.R;

import java.util.List;

public class ImageItemAdapter extends RecyclerView.Adapter<ImageItemAdapter.ImageItemViewHolder> {

    private List<ImageItem> imageItemList;
    private Context context;

    public ImageItemAdapter(Context context, List<ImageItem> imageItemList) {
        this.context = context;
        this.imageItemList = imageItemList;
    }

    @NonNull
    @Override
    public ImageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItemViewHolder holder, int position) {
        ImageItem imageItem = imageItemList.get(position);
        holder.imageView.setImageResource(imageItem.getImageResId());
    }

    @Override
    public int getItemCount() {
        return imageItemList.size();
    }

    static class ImageItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
