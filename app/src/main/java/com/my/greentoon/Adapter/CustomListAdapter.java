package com.my.greentoon.Adapter;// CustomListAdapter.java
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<Toon> {

    private Context context;
    private int resource;
    private List<Toon> toonList;

    public CustomListAdapter(Context context, int resource, List<Toon> toonList) {
        super(context, resource, toonList);
        this.context = context;
        this.resource = resource;
        this.toonList = toonList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(resource, null);
        }

        Toon toon = toonList.get(position);

        if (toon != null) {
            ImageView imgStoryCover = view.findViewById(R.id.imgStoryCover);
            TextView txtStoryName = view.findViewById(R.id.txtStoryName);
            TextView txtStoryGenre = view.findViewById(R.id.txtStoryGenre);
            TextView txtStoryDescription = view.findViewById(R.id.txtStoryDescription);

            Glide.with(context).load(toon.getStoryBookCover()).placeholder(R.drawable.sliderimg2).into(imgStoryCover);
            txtStoryName.setText(toon.getStoryName());
            txtStoryGenre.setText(toon.getStoryGenre());
            txtStoryDescription.setText(toon.getStoryDescription());
        }

        return view;
    }
}
