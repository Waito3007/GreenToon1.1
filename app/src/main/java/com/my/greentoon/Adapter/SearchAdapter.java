package com.my.greentoon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private Context context;
    private List<Toon> toonList;
    private List<Toon> filteredList;

    public SearchAdapter(Context context, List<Toon> toonList) {
        this.context = context;
        this.toonList = toonList;
        this.filteredList = new ArrayList<>(toonList);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Toon toon = filteredList.get(position);

        // Hiển thị thông tin của truyện
        viewHolder.toonName.setText(toon.getToonName());
        Glide.with(context).load(toon.getToonCover()).into(viewHolder.toonCover);

        return convertView;
    }

    public void filterList(List<Toon> filteredList) {
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView toonCover;
        TextView toonName;

        ViewHolder(View view) {
            toonCover = view.findViewById(R.id.iv_toon_cover);
            toonName = view.findViewById(R.id.tv_toon_name);
        }
    }
}
