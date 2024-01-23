package com.my.greentoon.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.my.greentoon.Adapter.ImageItemAdapter;
import com.my.greentoon.Adapter.ImageSliderAdapter;
import com.my.greentoon.Model.ImageItem;
import com.my.greentoon.R;
import com.my.greentoon.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    // slider
    private ViewPager2 viewPager;
    private ImageSliderAdapter imageSliderAdapter;
    //top truyen
    private ImageItemAdapter imageItemAdapter;
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set up ViewPager2 for image slider ( top truyen )
        viewPager = binding.getRoot().findViewById(R.id.viewPager);
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.sliderimg1);
        images.add(R.drawable.sliderimg2);
        images.add(R.drawable.sliderimg3);
        imageSliderAdapter = new ImageSliderAdapter(requireContext(), images);
        viewPager.setAdapter(imageSliderAdapter);

        // Set up RecyclerView with GridLayoutManager and ImageItemAdapter ( rank )
        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        List<ImageItem> imageItemList = generateImageItems(); // Your method to generate data
        imageItemAdapter = new ImageItemAdapter(requireContext(), imageItemList);
        recyclerView.setAdapter(imageItemAdapter);

        return view;

    }

    // Method to generate dummy data for the RecyclerView
    private List<ImageItem> generateImageItems() {
        List<ImageItem> imageItemList = new ArrayList<>();
        imageItemList.add(new ImageItem(R.drawable.truyen1,"Ten truyen 1"));
        imageItemList.add(new ImageItem(R.drawable.truyen2,"Ten Truyen 2"));
        imageItemList.add(new ImageItem(R.drawable.truyen3,"Ten truyen 3"));
        imageItemList.add(new ImageItem(R.drawable.truyen4,"Ten Truyen 4"));
        // Add more images as needed
        return imageItemList;
    }
}