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
import androidx.fragment.app.FragmentManager;
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
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    private ViewPager2 viewPager;
    private RecyclerView recyclerViewPopularToons;
    private TopToonAdapter topToonAdapter;
    private PopularToonAdapter popularToonAdapter;
    private List<Toon> toonList;
    private List<Toon> popularToonList;
    private Button btMoretoon,btNext,btPrev;
    private Timer autoScrollTimer; // Thêm biến Timer để theo dõi việc chuyển đổi tự động

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        recyclerViewPopularToons = view.findViewById(R.id.recyclerViewPopularToons);
        btMoretoon = view.findViewById(R.id.btMoretoon);
        btPrev = view.findViewById(R.id.btPrev);
        btNext = view.findViewById(R.id.btNext);
        toonList = new ArrayList<>();
        popularToonList = new ArrayList<>();
        //slider
        topToonAdapter = new TopToonAdapter(getContext(), toonList);
        viewPager.setAdapter(topToonAdapter);

        // slider
        popularToonAdapter = new PopularToonAdapter(popularToonList);
        recyclerViewPopularToons.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPopularToons.setAdapter(popularToonAdapter);
        loadTopToons();
        loadPopularToons();
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = (viewPager.getCurrentItem() + 1) % toonList.size();
                viewPager.setCurrentItem(nextItem, true); // Chuyển tới slider tiếp theo với animation
            }
        });

        btPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int prevItem = (viewPager.getCurrentItem() - 1 + toonList.size()) % toonList.size();
                viewPager.setCurrentItem(prevItem, true); // Trở về slider trước với animation
            }
        });
        btMoretoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo fragment SearchFragment
                SearchFragment searchFragment = new SearchFragment();

                // Thay thế HomeFragment bằng SearchFragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, searchFragment) // R.id.fragment_container là id của container fragment trong layout của activity
                        .addToBackStack(null) // Nếu bạn muốn quay lại HomeFragment khi nhấn nút Back, hãy thêm vào back stack
                        .commit();
            }
        });
        topToonAdapter.setOnItemClickListener(new TopToonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Toon toon) {
                // Khi click chuyển sang Detail
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("toonId", toon.getToonId());
                startActivity(intent);
            }
        });
        //
        popularToonAdapter.setOnItemClickListener(new PopularToonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Toon toon) {
                // Khi click chuyển sang Detail
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("toonId", toon.getToonId());
                startActivity(intent);
            }
        });

        // Bắt đầu tắt chuyển đổi tự động khi fragment được tạo
       // stopAutoScroll();
        // Bắt đầu chuyển đổi tự động khi fragment được tạo
        //startAutoScroll();
        return view;
    }

    // Hàm này bắt đầu chuyển đổi tự động
    private void startAutoScroll() {
        autoScrollTimer = new Timer();
        autoScrollTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Kiểm tra nếu fragment vẫn còn đính kèm vào activity
                if (isAdded()) {
                    // Thực hiện chuyển đổi tự động của ViewPager
                    viewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!toonList.isEmpty()) {
                                int currentItem = viewPager.getCurrentItem();
                                int nextItem = (currentItem + 1) % toonList.size();
                                viewPager.setCurrentItem(nextItem);
                            }
                        }
                    });
                }
            }
        }, 1900, 2100); // Chuyển slide sau mỗi 1.8s
    }

    // Hàm này dừng chuyển đổi tự động
    private void stopAutoScroll() {
        if (autoScrollTimer != null) {
            autoScrollTimer.cancel();
            autoScrollTimer = null;
        }
    }
    private void loadTopToons() {
        DatabaseReference toonsRef = FirebaseDatabase.getInstance().getReference("toons");
        Query query = toonsRef.orderByChild("viewCount").limitToLast(10);
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
                // lỗi
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

                // Random truyện
                int count = Math.min(9, allToons.size());
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoScroll();
    }
}
