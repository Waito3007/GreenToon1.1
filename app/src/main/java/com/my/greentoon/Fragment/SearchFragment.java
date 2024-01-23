package com.my.greentoon.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.my.greentoon.Adapter.SearchAdapter;
import com.my.greentoon.Model.SearchModel;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.Objects;


public class SearchFragment extends Fragment {

    int[] img ={R.drawable.truyen1,R.drawable.truyen2,R.drawable.truyen3,R.drawable.truyen4,R.drawable.truyen1};
    String[] name ={"truyen 1","truyen 2","truyen3","Truyen4","truyen5"};


    @SuppressLint("UseRequireInsteadOfGet")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        GridView gv = view.findViewById(R.id.gv);

        ArrayList<SearchModel> mylist = new ArrayList<>();

        for(int i = 0; i < name.length; i++)
        {
            mylist.add(new SearchModel(img[i],name[i]));
        }

        SearchAdapter myadapter = new SearchAdapter(getActivity(), R.layout.search_item, mylist);
        gv.setAdapter(myadapter);
        SearchView sv1 = view.findViewById(R.id.sv1);
        sv1.setQueryHint("Nhập tên truyện bạn muốn tìm");
        sv1.setIconifiedByDefault(false);
        sv1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myadapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                myadapter.getFilter().filter(query);
                return true;
            }
        });

        gv.setOnItemClickListener((parent, view1, position, id) -> {
            SearchModel item = myadapter.getItem(position);
            if (item != null) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "click " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}