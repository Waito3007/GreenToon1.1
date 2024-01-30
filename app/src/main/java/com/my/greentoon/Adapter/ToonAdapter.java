package com.my.greentoon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.List;

public class ToonAdapter extends ArrayAdapter<Toon> {

    private Context context;
    private List<Toon> toonList;

    public ToonAdapter(@NonNull Context context, int resource, @NonNull List<Toon> toonList) {
        super(context, resource, toonList);
        this.context = context;
        this.toonList = toonList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_toon, parent, false);
        }

        TextView textViewToonName = convertView.findViewById(R.id.textViewToonName);
        TextView textViewToonDesc = convertView.findViewById(R.id.textViewToonDesc);
        ImageView imageViewToonCover = convertView.findViewById(R.id.imageViewToonCover);

        Toon currentToon = toonList.get(position);

        textViewToonName.setText(currentToon.getToonName());
        textViewToonDesc.setText(currentToon.getToonDes());

        // Sử dụng thư viện Glide để hiển thị ảnh từ URL
        Glide.with(context)
                .load(currentToon.getToonCover())
                .into(imageViewToonCover);

        return convertView;
    }
}
