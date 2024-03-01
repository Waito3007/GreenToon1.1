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

public class PopularToonAdapter extends RecyclerView.Adapter<PopularToonAdapter.ViewHolder> {
    private List<Toon> toonList;
    private OnItemClickListener listener;

    public PopularToonAdapter(List<Toon> toonList) {
        this.toonList = toonList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Toon toon);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_toon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Toon toon = toonList.get(position);

        holder.toonNameTextView.setText(toon.getToonName());
        holder.toonViewCountTextView.setText(String.valueOf(toon.getViewCount()));

        Picasso.get().load(toon.getToonCover()).into(holder.toonCoverImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(toon);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return toonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView toonCoverImageView;
        private TextView toonNameTextView;
        private TextView toonViewCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            toonCoverImageView = itemView.findViewById(R.id.toon_cover);
            toonNameTextView = itemView.findViewById(R.id.toon_name);
            toonViewCountTextView = itemView.findViewById(R.id.toon_view_count);
        }
    }
}
