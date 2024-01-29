package com.my.greentoon.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ToonAdapter extends RecyclerView.Adapter<ToonAdapter.ViewHolder> {

    private List<Toon> toonList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Toon toon);
        void onDeleteClick(Toon toon);
    }

    public ToonAdapter(List<Toon> toonList, OnItemClickListener listener) {
        this.toonList = toonList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_toon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Toon toon = toonList.get(position);
        holder.bind(toon, listener);
    }

    @Override
    public int getItemCount() {
        return toonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgBookCover;
        private TextView txtStoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBookCover = itemView.findViewById(R.id.imgBookCover);
            txtStoryName = itemView.findViewById(R.id.txtStoryName);
        }

        public void bind(final Toon toon, final OnItemClickListener listener) {
            txtStoryName.setText(toon.getStoryName());
            Picasso.get().load(toon.getStoryBookCover()).into(imgBookCover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(toon);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onDeleteClick(toon);
                    return true;
                }
            });
        }
    }
}
