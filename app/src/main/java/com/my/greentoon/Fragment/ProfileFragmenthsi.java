package com.my.greentoon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

public class ProfileFragmenthsi extends Fragment {
    private Button btName;
    private TextView tvfollow;
    private ImageButton imgAvatar,imgbsetting;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ImageButton btLogout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_fragmenthsi, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        imgbsetting = view.findViewById(R.id.imgbsetting);
        btName = view.findViewById(R.id.btName);
        btLogout = view.findViewById(R.id.btLogout);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvfollow = view.findViewById(R.id.tvfollow);
        btLogout.setOnClickListener(v -> logoutUser());

        tvfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WatchListFragment watchListFragment = new WatchListFragment();
                // Lấy reference của FragmentManager
                FragmentManager fragmentManager = getParentFragmentManager(); // hoặc getChildFragmentManager() tùy vào ngữ cảnh

                // Bắt đầu một giao dịch Fragment
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Thay thế Fragment hiện tại bằng UploadStoryFragment
                transaction.replace(R.id.fragment_container, watchListFragment);

                // Thêm transaction vào Back Stack (nếu cần)
                transaction.addToBackStack(null);

                // Hoàn thành giao dịch
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateProfile();
        imgbsetting.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
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
                databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User userProfile = snapshot.getValue(User.class);
                            if (userProfile != null) {
                                // Hiển thị button và avatar khi đã đăng nhập
                                btName.setVisibility(View.VISIBLE);
                                imgAvatar.setVisibility(View.VISIBLE);
                                btLogout.setVisibility(View.VISIBLE);
                                imgbsetting.setVisibility(View.VISIBLE);
                                // Cập nhật tên người dùng
                                String displayName = userProfile.getNameUser();
                                btName.setText((displayName != null && !displayName.isEmpty()) ? displayName : "Người Dùng");

                                // Cập nhật avatar
                                String avatarUrl = userProfile.getAvatarUser();
                                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                    // Sử dụng thư viện Glide hoặc Picasso để tải ảnh về imgAvatar
                                    Glide.with(ProfileFragmenthsi.this)
                                            .load(avatarUrl)
                                            .placeholder(R.drawable.ic_default_avatar) // Hình ảnh mặc định khi đang tải
                                            .error(R.drawable.ic_default_avatar) // Hình ảnh khi có lỗi
                                            .circleCrop() // Áp dụng góc cong để tạo hình tròn
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
                // Người dùng chưa đăng nhập, hiển thị button đăng nhập
                btName.setVisibility(View.GONE);
                imgAvatar.setVisibility(View.GONE); // GONE nếu ẩn avatar khi chưa đăng nhập
                imgAvatar.setImageResource(R.drawable.logohouhou);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ProfileFragment", "Error updating login button text and avatar: " + e.getMessage());
        }
    }
}

