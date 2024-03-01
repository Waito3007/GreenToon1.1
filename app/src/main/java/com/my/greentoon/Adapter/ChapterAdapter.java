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

public class ChapterAdapter extends ArrayAdapter<Chapter> {

    private Context mContext;
    private int mResource;

    public ChapterAdapter(@NonNull Context context, int resource, @NonNull List<Chapter> chapters) {
        super(context, resource, chapters);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        TextView textViewChapterName = convertView.findViewById(R.id.textViewChapterName);

        Chapter chapter = getItem(position);

        if (chapter != null) {
            textViewChapterName.setText(chapter.getChapterName());
        }

        return convertView;
    }
}
