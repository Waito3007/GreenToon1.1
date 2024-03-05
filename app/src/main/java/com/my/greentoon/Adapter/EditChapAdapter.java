package com.my.greentoon.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.my.greentoon.Model.Chapter;
import com.my.greentoon.R;

import java.util.List;

public class EditChapAdapter extends ArrayAdapter<Chapter> {

    private Context mContext;
    private int mResource;

    public EditChapAdapter(@NonNull Context context, int resource, @NonNull List<Chapter> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String chapterName = getItem(position).getChapterName();
        String chapterTitle = getItem(position).getChapterTitle();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvChapterName = convertView.findViewById(R.id.tvChapterName);
        TextView tvChapterTitle = convertView.findViewById(R.id.tvChapterTitle);

        tvChapterName.setText(chapterName);
        tvChapterTitle.setText(chapterTitle);

        return convertView;
    }
}
