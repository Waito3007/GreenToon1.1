package com.my.greentoon.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.my.greentoon.Model.User;
import com.my.greentoon.R;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<User> {
    private Context mContext;

    public UserListAdapter(Context context, ArrayList<User> list) {
        super(context, 0, list);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.user_list_item, parent, false);
        }

        // Thiết lập góc bo cho từng hàng
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{50, 50, 50, 50, 50, 50, 50, 50}); // Góc bo cho cả 4 góc
        shape.setColor(Color.WHITE); // Màu nền của hàng
        listItem.setBackground(shape);

        // Lấy TextView từ layout user_list_item.xml
        TextView usernameTextView = listItem.findViewById(R.id.usernameTextView);

        // Lấy đối tượng User tại vị trí position
        User currentUser = getItem(position);

        // Thiết lập dữ liệu cho TextView
        if (currentUser != null) {
            usernameTextView.setText(currentUser.getNameUser());
        }

        return listItem;
    }
}


