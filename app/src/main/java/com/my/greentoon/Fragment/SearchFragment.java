package com.my.greentoon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Activity.DetailActivity;
import com.my.greentoon.Adapter.SearchAdapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private GridView gridView;
    private SearchAdapter searchAdapter;
    private List<Toon> toonList;
    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = root.findViewById(R.id.search_view);
        gridView = root.findViewById(R.id.gv);
        toonList = new ArrayList<>();
        searchAdapter = new SearchAdapter(getContext(), toonList);
        gridView.setAdapter(searchAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("toons");

        // Listen for search input
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchToons(newText);
                return true;
            }
        });

        // Load cartoon list from Firebase
        loadToonList();

        // Handle item click
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected toon
                Toon selectedToon = toonList.get(position);

                // Navigate to DetailActivity and pass the selected toon ID
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("toonId", selectedToon.getToonId());
                startActivity(intent);
            }
        });

        return root;
    }

    private void loadToonList() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toonList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Toon toon = snapshot.getValue(Toon.class);
                    if (toon != null) {
                        toonList.add(toon);
                    }
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void searchToons(String query) {
        List<Toon> filteredList = new ArrayList<>();
        for (Toon toon : toonList) {
            if (toon.getToonName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(toon);
            }
        }
        searchAdapter.filterList(filteredList);
    }
}
