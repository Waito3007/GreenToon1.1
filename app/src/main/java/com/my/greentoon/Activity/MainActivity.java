package com.my.greentoon.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.my.greentoon.Fragment.HomeFragment;
import com.my.greentoon.Fragment.ProfileFragment;
import com.my.greentoon.Fragment.ProfileFragmenthsi;
import com.my.greentoon.Fragment.SearchFragment;
import com.my.greentoon.Fragment.WatchlistFragment;
import com.my.greentoon.R;

public class MainActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    BottomNavigationView navigationView;
    FirebaseAuth mAuth; // Thêm đối tượng FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        navigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.body_container , new HomeFragment()).commit();
        navigationView.setSelectedItemId(R.id.nav_home);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()){

                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.nav_user:
                        // Kiểm tra xem người dùng đã đăng nhập hay chưa
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if(currentUser != null) {
                            fragment = new ProfileFragmenthsi(); // Người dùng đã đăng nhập
                        } else {
                            fragment = new ProfileFragment(); // Người dùng chưa đăng nhập
                        }
                        break;
                    case R.id.nav_follow:
                        fragment = new WatchlistFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.body_container,fragment).commit();


                return true;
            }
        });
    }
}
