package com.my.greentoon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Model.User;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private ListView listViewUsers;
    private ArrayList<User> userList;
    private ArrayAdapter<User> arrayAdapter;
    private ArrayList<User> originalUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Khởi tạo và hiển thị danh sách người dùng
        listViewUsers = findViewById(R.id.listViewUsers);
        userList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listViewUsers.setAdapter(arrayAdapter);

        // Lưu danh sách người dùng ban đầu
        originalUserList = new ArrayList<>();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        // Lấy danh sách người dùng từ Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                originalUserList.clear(); // Xóa danh sách người dùng ban đầu
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                    originalUserList.add(user); // Thêm người dùng vào danh sách ban đầu
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Toast.makeText(UserListActivity.this, "Không thể lấy danh sách người dùng", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện khi người dùng chọn một mục từ danh sách
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            // Lấy ID của người dùng được chọn từ danh sách
            String selectedUserId = userList.get(position).getUserId();

            // Tạo Intent để chuyển sang DetailEditUserActivity
            Intent intent = new Intent(UserListActivity.this, DetailEditUserActivity.class);

            // Truyền ID của người dùng được chọn sang DetailEditUserActivity
            intent.putExtra("selectedUserId", selectedUserId);

            // Chuyển hướng sang DetailEditUserActivity
            startActivity(intent);
        });
    }

    private void filter(String query) {
        List<User> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(originalUserList); // Sử dụng danh sách ban đầu khi query rỗng
        } else {
            for (User user : userList) {
                if (user.getNameUser().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }
        arrayAdapter.clear();
        arrayAdapter.addAll(filteredList);
        arrayAdapter.notifyDataSetChanged();
    }
}
