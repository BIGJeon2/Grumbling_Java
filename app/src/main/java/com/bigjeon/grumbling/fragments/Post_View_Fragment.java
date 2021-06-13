package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentPostViewBinding;

import java.util.ArrayList;

public class Post_View_Fragment extends Fragment {
    private FragmentPostViewBinding binding;
    private Context context;
    ArrayList<Post_Data> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_view, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_post_view, container, false);
        list.add(new Post_Data("희태", null, "BIg", "확인합시다", "ALL_USER", 10,R.color.black, R.color.white, "now", R.drawable.multi_colred_motion, 0, 0));
        RecyclerView rcv = binding.PostRecyclerView;
        Post_View_Rcv_Adapter adapter = new Post_View_Rcv_Adapter(list);
        LinearLayoutManager lm = new LinearLayoutManager(v.getContext());
        rcv.setLayoutManager(lm);
        rcv.setAdapter(adapter);
        rcv.setHasFixedSize(true);
        return root;
    }
}