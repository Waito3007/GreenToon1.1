package com.my.greentoon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import com.my.greentoon.Activity.DetailActivity;
import com.my.greentoon.Adapter.ToonAdapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class WatchListFragment extends Fragment {

    private ListView listViewWatchList;
    private DatabaseReference databaseReference;
    private List<Toon> watchList;
    private ToonAdapter watchListAdapter;

    public WatchListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_watch_list, container, false);

        listViewWatchList = rootView.findViewById(R.id.listViewWatchList);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        watchList = new ArrayList<>();
        watchListAdapter = new ToonAdapter(getContext(), R.layout.item_toon, watchList);
        listViewWatchList.setAdapter(watchListAdapter);

        // Bắt sự kiện khi người dùng nhấp vào một mục trong danh sách
        listViewWatchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy ID của truyện được chọn
                String toonId = watchList.get(position).getToonId();

                // Chuyển sang DetailActivity và truyền toonId của truyện được chọn
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("toonId", toonId);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hiển thị danh sách truyện đã theo dõi khi Fragment được tạo
        displayWatchList();
    }

    private void displayWatchList() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference watchListRef = databaseReference.child("follows").child(currentUserId);
            watchListRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    watchList.clear();
                    for (DataSnapshot toonSnapshot : dataSnapshot.getChildren()) {
                        String toonId = toonSnapshot.getKey();
                        DatabaseReference toonRef = databaseReference.child("toons").child(toonId);
                        toonRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Toon toon = dataSnapshot.getValue(Toon.class);
                                if (toon != null) {
                                    watchList.add(toon);
                                    watchListAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), "Failed to load watchlist: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load watchlist: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
