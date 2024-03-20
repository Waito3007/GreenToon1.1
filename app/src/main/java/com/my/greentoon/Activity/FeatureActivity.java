package com.my.greentoon.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.my.greentoon.Fragment.ChatFragment;
import com.my.greentoon.Fragment.PlatformFragment;
import com.my.greentoon.Fragment.PremiumFragment;
import com.my.greentoon.Fragment.StoryGenreFragment;
import com.my.greentoon.R;

public class FeatureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new StoryGenreFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.story_genre:
                            selectedFragment = new StoryGenreFragment();
                            break;
                        case R.id.platform:
                            selectedFragment = new PlatformFragment();
                            break;
                        case R.id.chat:
                            selectedFragment = new ChatFragment();
                            break;
                        case R.id.premium:
                            selectedFragment = new PremiumFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };
}
