package com.my.greentoon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class SearchFragment extends Fragment {

    private AutoCompleteTextView autoCompleteTextViewSearch;
    private ListView listViewSearchResults;

    private DatabaseReference databaseReference;
    private List<Toon> searchResults;
    private ToonAdapter searchAdapter;

    private List<String> allToonNames;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        autoCompleteTextViewSearch = rootView.findViewById(R.id.autoCompleteTextViewSearch);
        listViewSearchResults = rootView.findViewById(R.id.listViewSearchResults);

        databaseReference = FirebaseDatabase.getInstance().getReference("toons");
        searchResults = new ArrayList<>();

        allToonNames = new ArrayList<>();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Thiết lập AutoCompleteTextView và ListView
        setupAutoCompleteTextView();
        setupListView();

        // Hiển thị toàn bộ danh sách toon khi fragment được tạo
        displayAllToons();
    }

    private void setupAutoCompleteTextView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, allToonNames);
        autoCompleteTextViewSearch.setAdapter(adapter);

        autoCompleteTextViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedToonName = (String) parent.getItemAtPosition(position);
                searchToons(selectedToonName);
            }
        });
    }

    private void setupListView() {
        searchAdapter = new ToonAdapter(getContext(), R.layout.item_toon, searchResults);
        listViewSearchResults.setAdapter(searchAdapter);

        // Xử lý sự kiện khi người dùng nhấp vào một mục trong danh sách kết quả tìm kiếm
        listViewSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy thông tin của toon được chọn
                Toon selectedToon = searchResults.get(position);

                // Chuyển sang DetailActivity và truyền toonId của toon được chọn
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("toonId", selectedToon.getToonId());
                startActivity(intent);
            }
        });
    }

    private void displayAllToons() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allToonNames.clear();
                searchResults.clear();
                for (DataSnapshot toonSnapshot : dataSnapshot.getChildren()) {
                    Toon toon = toonSnapshot.getValue(Toon.class);
                    if (toon != null) {
                        allToonNames.add(toon.getToonName());
                        searchResults.add(toon);
                    }
                }
                // Cập nhật adapter để hiển thị toàn bộ danh sách toon
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load toons: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchToons(final String searchTerm) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResults.clear();
                for (DataSnapshot toonSnapshot : dataSnapshot.getChildren()) {
                    Toon toon = toonSnapshot.getValue(Toon.class);
                    if (toon != null && toon.getToonName() != null && toon.getToonName().toLowerCase().contains(searchTerm.toLowerCase())) {
                        searchResults.add(toon);
                    }
                }
                // Cập nhật adapter để hiển thị danh sách toon đã được lọc
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to search: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
