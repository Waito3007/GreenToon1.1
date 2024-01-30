package com.my.greentoon.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Activity.EditProfileActivity;
import com.my.greentoon.Activity.SignInActivity;
import com.my.greentoon.Model.User;
import com.my.greentoon.R;

public class ProfileFragment extends Fragment {

    private Button btnLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ImageButton btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        btnLogin = view.findViewById(R.id.btLogin);
        btnLogout = view.findViewById(R.id.btLogout);

        btnLogout.setOnClickListener(v -> logoutUser());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateLoginButtonText();
        btnLogin.setOnClickListener(v -> handleLoginButtonClick());
    }

    private void handleLoginButtonClick() {
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ProfileFragment", "Error handling login button click: " + e.getMessage());
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if(getActivity() != null) {
            getActivity().finish();
        }
    }

    private void updateLoginButtonText() {
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User userProfile = snapshot.getValue(User.class);
                            if (userProfile != null) {
                                String displayName = userProfile.getNameUser();
                                btnLogin.setText((displayName != null && !displayName.isEmpty()) ? displayName : "Người Dùng");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ProfileFragment", "Error fetching user data: " + error.getMessage());
                    }
                });
            } else {
                btnLogin.setText("Nhấn vào đây để đăng nhập hoặc đăng ký");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ProfileFragment", "Error updating login button text: " + e.getMessage());
        }
    }
}
