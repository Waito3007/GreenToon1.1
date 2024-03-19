package com.my.greentoon.Fragment;

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
import com.my.greentoon.Adapter.ToonAdapter;
import com.my.greentoon.Model.FollowedToon;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.Model.User;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WatchListFragment extends Fragment {

    private ListView listViewToons;
    private List<Toon> toonList;
    private ToonAdapter adapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        listViewToons = view.findViewById(R.id.listViewToons);
        toonList = new ArrayList<>();
        adapter = new ToonAdapter(requireContext(), R.layout.item_toon, toonList);
        listViewToons.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("followedToons").child(userId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    toonList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FollowedToon followedToon = snapshot.getValue(FollowedToon.class);
                        if (followedToon != null && followedToon.isFavorite()) {
                            loadToonDetails(followedToon.getToonId());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load watch list: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        listViewToons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click here, for example, navigate to detail activity
            }
        });

        return view;
    }

    private void loadToonDetails(String toonId) {
        DatabaseReference toonRef = FirebaseDatabase.getInstance().getReference("toons").child(toonId);
        toonRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toon toon = dataSnapshot.getValue(Toon.class);
                if (toon != null) {
                    toonList.add(toon);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load toon details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
