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
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.List;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.GenresViewHolder> {
    private Context mContext;
    private List<Toon> mToonList;

    public GenresAdapter(Context context, List<Toon> toonList) {
        mContext = context;
        mToonList = toonList;
    }

    @NonNull
    @Override
    public GenresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_toon, parent, false);
        return new GenresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenresViewHolder holder, int position) {
        Toon currentToon = mToonList.get(position);
        holder.textViewToonName.setText(currentToon.getToonName());
        // Load image using Glide or any other image loading library
        Glide.with(mContext)
                .load(currentToon.getToonCover())
                .placeholder(R.drawable.default_avatar) // Placeholder image while loading
                .into(holder.imageViewToonCover);
    }

    @Override
    public int getItemCount() {
        return mToonList.size();
    }

    public class GenresViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewToonCover;
        public TextView textViewToonName;

        public GenresViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewToonCover = itemView.findViewById(R.id.imageViewToonCover);
            textViewToonName = itemView.findViewById(R.id.textViewToonName);

            // Add onClickListener if needed
        }
    }
}
