package com.my.greentoon.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Model.User;
import com.my.greentoon.R;

public class DetailEditUserActivity extends AppCompatActivity {

    private TextView txtUserId, txtEmail, txtNameUser;
    private Button  btnDeleteUser, btnToggleAdmin, btBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit_user);

        // Ánh xạ các view từ layout
        txtUserId = findViewById(R.id.txtUserId);
        txtEmail = findViewById(R.id.txtEmail);
        txtNameUser = findViewById(R.id.txtNameUser);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        btnToggleAdmin = findViewById(R.id.btnToggleAdmin);
        btBack = findViewById(R.id.btBack);

        // Lấy ID của người dùng được chọn từ Intent
        final String selectedUserId = getIntent().getStringExtra("selectedUserId");

        // Tạo DatabaseReference để truy vấn thông tin của người dùng từ Firebase Realtime Database
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(selectedUserId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lấy thông tin người dùng từ DataSnapshot
                final User user = dataSnapshot.getValue(User.class);

                // Hiển thị thông tin người dùng lên TextViews
                if (user != null) {
                    txtUserId.setText("User ID: " + user.getUserId());
                    txtEmail.setText("Email: " + user.getEmail());
                    txtNameUser.setText("Name: " + user.getNameUser());
                    // Bắt sự kiện xóa người dùng
                    btnDeleteUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteUser(selectedUserId);
                            finish();
                        }
                    });
                    btBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    // Bắt sự kiện chuyển đổi quyền admin
                    btnToggleAdmin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleAdmin(databaseReference, user);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy vấn dữ liệu
            }
        });
    }


    // Phương thức xóa người dùng
    private void deleteUser(final String userId) {
        // Tạo DatabaseReference để truy vấn dữ liệu người dùng từ Firebase Realtime Database
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Xóa người dùng từ cơ sở dữ liệu
        userReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Thông báo xóa thành công
                        Toast.makeText(DetailEditUserActivity.this, "Đã xóa người dùng này", Toast.LENGTH_SHORT).show();
                        // Kết thúc activity và trở về trang trước
                        finish();

                        // Sau khi xóa thành công từ Realtime Database, tiến hành xóa từ Authentication
                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Xóa thành công từ Authentication
                                        Toast.makeText(DetailEditUserActivity.this, "Đã xóa người dùng từ Authentication", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Xử lý khi có lỗi xảy ra khi xóa từ Authentication
                                        Toast.makeText(DetailEditUserActivity.this, "Thất bại khi xóa từ Authentication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi có lỗi xảy ra
                        Toast.makeText(DetailEditUserActivity.this, "Thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Phương thức chuyển đổi quyền admin
    private void toggleAdmin(DatabaseReference databaseReference, User user) {
        // Đảm bảo user không null trước khi thực hiện
        if (user != null) {
            // Đảo ngược trạng thái isAdmin
            boolean isAdmin = !user.isAdmin();
            // Cập nhật isAdmin trong database
            databaseReference.child("admin").setValue(isAdmin);
            if (isAdmin){
                Toast.makeText(this, "đã trở thành Admin", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "đã trở thành User", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
