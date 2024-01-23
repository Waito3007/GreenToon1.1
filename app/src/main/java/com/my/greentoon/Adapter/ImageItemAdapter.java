package com.my.greentoon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

        ImageItem item = imageItemList.get(position);

        holder.imageView.setImageResource(item.getImageResId());
        holder.tenTruyen.setText(item.getTenTruyen());

    }

    @Override
    public int getItemCount() {
        return imageItemList.size();
    }

    static class ImageItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        TextView tenTruyen;
        ImageItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tenTruyen = itemView.findViewById(R.id.item_name);
        }
    }
}

