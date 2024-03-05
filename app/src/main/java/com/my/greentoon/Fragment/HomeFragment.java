package com.my.greentoon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Activity.DetailActivity;
import com.my.greentoon.Adapter.PopularToonAdapter;
import com.my.greentoon.Adapter.TopToonAdapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ViewPager2 viewPager;
    private RecyclerView recyclerViewPopularToons;
    private TopToonAdapter topToonAdapter;
    private PopularToonAdapter popularToonAdapter;
    private List<Toon> toonList;
    private List<Toon> popularToonList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        recyclerViewPopularToons = view.findViewById(R.id.recyclerViewPopularToons);

        toonList = new ArrayList<>();
        popularToonList = new ArrayList<>();

        // Initialize adapter for the top slideshow
        topToonAdapter = new TopToonAdapter(getContext(), toonList);
        viewPager.setAdapter(topToonAdapter);

        // Initialize adapter for the popular toons list below the slideshow
        popularToonAdapter = new PopularToonAdapter(popularToonList);
        recyclerViewPopularToons.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPopularToons.setAdapter(popularToonAdapter);

        loadTopToons();
        loadPopularToons();

        // Set item click listener for the popular toons list
        popularToonAdapter.setOnItemClickListener(new PopularToonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Toon toon) {
                // Handle item click, navigate to detail activity
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("toonId", toon.getToonId());
                startActivity(intent);
            }
        });
        return view;
    }
    private void loadTopToons() {
        DatabaseReference toonsRef = FirebaseDatabase.getInstance().getReference("toons");
        Query query = toonsRef.orderByChild("viewCount").limitToLast(4);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toonList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Toon toon = snapshot.getValue(Toon.class);
                    if (toon != null) {
                        toonList.add(toon);
                    }
                }
                topToonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void loadPopularToons() {
        DatabaseReference toonsRef = FirebaseDatabase.getInstance().getReference("toons");
        toonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                popularToonList.clear();
                List<Toon> allToons = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Toon toon = snapshot.getValue(Toon.class);
                    if (toon != null) {
                        allToons.add(toon);
                    }
                }

                // Add 4 random toons to the popular list
                int count = Math.min(4, allToons.size());
                for (int i = 0; i < count; i++) {
                    popularToonList.add(allToons.get(i));
                }

                popularToonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
