package com.my.greentoon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.my.greentoon.Activity.SignInActivity;
import com.my.greentoon.R;

public class ProfileFragment extends Fragment {

    // ... Các khai báo khác
    Button btLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Tìm nút có id là btLogin
        btLogin = view.findViewById(R.id.btLogin);

        // Thiết lập sự kiện click cho nút btLogin
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển từ fragment_profile sang activiti_sign_in
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}