package com.my.greentoon.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.StatusAdapter;
import com.my.greentoon.Model.Status;
import com.my.greentoon.Model.User;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class PlatformFragment extends Fragment {

    private ImageView imageViewUserAvatar;
    private Button buttonPostStatus;

    private RecyclerView recyclerViewStatus;
    private StatusAdapter statusAdapter;
    private List<Status> statusList;

    private DatabaseReference userRef;
    private DatabaseReference statusRef;
    private User currentUser; // Thêm biến này để lưu trữ thông tin người dùng hiện tại

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_platform, container, false);

        imageViewUserAvatar = root.findViewById(R.id.imageViewUserAvatar);
        buttonPostStatus = root.findViewById(R.id.buttonPostStatus);
        recyclerViewStatus = root.findViewById(R.id.recyclerViewStatus);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentUser = snapshot.getValue(User.class);
                        if (currentUser != null) {
                            // Load hình ảnh và áp dụng circleCrop để tạo hình tròn
                            Glide.with(requireContext())
                                    .load(currentUser.getAvatarUser())
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_default_avatar) // Hình ảnh mặc định khi đang tải
                                    .error(R.drawable.ic_default_avatar) // Hình ảnh khi có lỗi
                                    .into(imageViewUserAvatar);
                            // Tạo adapter sau khi nhận được thông tin người dùng
                            setupRecyclerView();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        buttonPostStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo một instance của UploadStoryFragment
                UploadStoryFragment uploadStoryFragment = new UploadStoryFragment();

                // Lấy reference của FragmentManager
                FragmentManager fragmentManager = getParentFragmentManager(); // hoặc getChildFragmentManager() tùy vào ngữ cảnh

                // Bắt đầu một giao dịch Fragment
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Thay thế Fragment hiện tại bằng UploadStoryFragment
                transaction.replace(R.id.fragment_container, uploadStoryFragment);

                // Thêm transaction vào Back Stack (nếu cần)
                transaction.addToBackStack(null);

                // Hoàn thành giao dịch
                transaction.commit();
            }
        });


        return root;
    }

    private void setupRecyclerView() {
        // Hiển thị danh sách status
        recyclerViewStatus.setHasFixedSize(true);
        recyclerViewStatus.setLayoutManager(new LinearLayoutManager(getContext()));
        statusList = new ArrayList<>();
        // Kiểm tra currentUser đã được khởi tạo chưa
        if (currentUser != null) {
            statusAdapter = new StatusAdapter(getContext(), statusList, currentUser);
            recyclerViewStatus.setAdapter(statusAdapter);
        }
        // Load danh sách status từ database
        statusRef = FirebaseDatabase.getInstance().getReference().child("status");
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statusList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Status status = dataSnapshot.getValue(Status.class);
                    statusList.add(status);
                }
                statusAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
