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

import com.bumptech.glide.Glide;
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
    ImageButton imgAvatar;
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
        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnLogout.setOnClickListener(v -> logoutUser());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateProfile();
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
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
    private void updateProfile() {
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                // Kiểm tra xem email đã được xác nhận hay chưa
                boolean isEmailVerified = currentUser.isEmailVerified();

                // Bổ sung thêm điều kiện kiểm tra email đã xác nhận hay chưa
                databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User userProfile = snapshot.getValue(User.class);
                            if (userProfile != null) {
                                // Cập nhật nút đăng nhập
                                String displayName = userProfile.getNameUser();
                                btnLogin.setText((displayName != null && !displayName.isEmpty()) ? displayName : "Người Dùng");

                                // Cập nhật avatar
                                String avatarUrl = userProfile.getAvatarUser();
                                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                    // Sử dụng thư viện Glide hoặc Picasso để tải ảnh về imgAvatar
                                    // Ví dụ sử dụng Glide:
                                    Glide.with(ProfileFragment.this)
                                            .load(avatarUrl)
                                            .placeholder(R.drawable.ic_default_avatar) // Hình ảnh mặc định khi đang tải
                                            .error(R.drawable.ic_default_avatar) // Hình ảnh khi có lỗi
                                            .into(imgAvatar);
                                } else {
                                    // Nếu không có avatar, hiển thị hình ảnh mặc định
                                    imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ProfileFragment", "Error fetching user data: " + error.getMessage());
                    }
                });
            } else {
                // Người dùng chưa đăng nhập, cập nhật nút đăng nhập và avatar về giá trị mặc định
                btnLogin.setText("Nhấn vào đây để đăng nhập hoặc đăng ký");
                imgAvatar.setImageResource(R.drawable.logohouhou);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ProfileFragment", "Error updating login button text and avatar: " + e.getMessage());
        }
    }
}

