package com.my.greentoon.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.ToonAdapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class StoryGenreFragment extends Fragment {

    private List<Toon> toonList;
    private ListView listViewToons;
    private ToonAdapter toonAdapter;

    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_story_genre, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("toons");

        listViewToons = root.findViewById(R.id.listViewToons);

        toonList = new ArrayList<>();
        toonAdapter = new ToonAdapter(getContext(), R.layout.item_toon, toonList);
        listViewToons.setAdapter(toonAdapter);

        // Get toons based on selected genre
        Button btnAction = root.findViewById(R.id.btnAction);
        Button btnDrama = root.findViewById(R.id.btnDrama);
        Button btnFantasy = root.findViewById(R.id.btnFantasy);
        Button btnChuyensinh = root.findViewById(R.id.btnChuyensinh);
        Button btnManhwa = root.findViewById(R.id.btnManhwa);
        Button btnNgontinh = root.findViewById(R.id.btnNgontinh);
        Button btnTrinhtham = root.findViewById(R.id.btnTrinhtham);
        Button btnTutien = root.findViewById(R.id.btnTutien);
        // Add buttons for other genres here

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToonsByGenre("Action");
            }
        });
        btnChuyensinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToonsByGenre("Chuyển Sinh");
            }
        });

        btnDrama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToonsByGenre("Drama");
            }
        });

        btnFantasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToonsByGenre("Fantasy");
            }
        });
        btnManhwa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToonsByGenre("Manhwa");
            }
        });
        btnNgontinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToonsByGenre("Ngôn Tình");
            }
        });
        btnTrinhtham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToonsByGenre("Trinh thám");
            }
        });
        btnTutien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToonsByGenre("Tu Tiên");
            }
        });

        return root;
    }

    private void loadToonsByGenre(String genre) {
        Query query = databaseReference.orderByChild("genres/" + genre).equalTo(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                toonList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Toon toon = dataSnapshot.getValue(Toon.class);
                    toonList.add(toon);
                }
                toonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
