package com.my.greentoon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.my.greentoon.Activity.SignInActivity;
import com.my.greentoon.Activity.SignUpActivity;
import com.my.greentoon.R;

public class ProfileFragment extends Fragment {

    private Button btLogin, btSignup;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        btLogin = view.findViewById(R.id.btLogin);
        btSignup = view.findViewById(R.id.btSignup);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
        });
        btSignup.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignUpActivity.class);
            startActivity(intent);
        });
    }
}





