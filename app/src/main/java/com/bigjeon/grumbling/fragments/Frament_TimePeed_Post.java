package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.adapter.Timepeed_Rcv_Friends_Adapter;
import com.bigjeon.grumbling.adapter.Timepeed_Rcv_Post_Adapter;
import com.bigjeon.grumbling.data.Notification_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentTimepeedPostBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Frament_TimePeed_Post extends Fragment {
    private FragmentTimepeedPostBinding binding;
    private String My_Uid;
    private String My_Img;
    private String My_Name;
    private Timepeed_Rcv_Post_Adapter adapter;
    private ArrayList<Notification_Data> TimePeed_Post_List = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timepeed_post, container, false);
        View root = binding.getRoot();

        Get_My_Profile();

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        adapter = new Timepeed_Rcv_Post_Adapter(getContext(), TimePeed_Post_List, My_Uid);
        binding.TimepeedPostRCV.setAdapter(adapter);
        binding.TimepeedPostRCV.setLayoutManager(lm);
        binding.TimepeedPostRCV.setHasFixedSize(true);
        binding.TimepeedPostRCV.setNestedScrollingEnabled(false);

        adapter.Get_Post_Timepeed();
        adapter.notifyDataSetChanged();

        binding.TimepeedPostSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.List_Clear();
                adapter.Get_Post_Timepeed();
                adapter.notifyDataSetChanged();
                binding.TimepeedPostSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }
    private void Get_My_Profile(){
        SharedPreferences Get_My_Data = this.getActivity().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = Get_My_Data.getString("UID", null);
        My_Name = Get_My_Data.getString("NAME", null);
        My_Img = Get_My_Data.getString("IMG", null);
    }
}