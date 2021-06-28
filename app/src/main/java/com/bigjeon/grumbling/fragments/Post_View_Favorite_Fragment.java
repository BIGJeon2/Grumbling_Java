package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentFavoritePostViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Post_View_Favorite_Fragment extends Fragment {

    public static Context mcontext;
    private String My_Uid;
    private FragmentFavoritePostViewBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference DB;
    private Post_View_Rcv_Adapter adapter;
    private String Get_Content_Grade = "좋아요 게시글";
    ArrayList<Post_Data> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_post_view, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_favorite_post_view, container, false);

        mcontext = this.getContext();
        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference("Posts");

        My_Uid = mAuth.getCurrentUser().getUid();

        RecyclerView rcv = binding.PostRecyclerView;
        adapter = new Post_View_Rcv_Adapter(getContext(), list, Get_Content_Grade);
        LinearLayoutManager lm = new LinearLayoutManager(v.getContext());
        rcv.setLayoutManager(lm);
        rcv.setAdapter(adapter);
        rcv.setHasFixedSize(true);

        Get_Post();

        binding.PostSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                adapter.notifyDataSetChanged();
                Get_Post();
                binding.PostSwipeRefreshLayout.setRefreshing(false);
            }
        });

        binding.PostWritePostStartCircleImg.setOnClickListener(V -> Alert_Post_Write_Dialog());
        return root;
    }

    private void Alert_Post_Write_Dialog() {
        Post_Write_Fragment post_write_fragment = Post_Write_Fragment.getInstance();
        post_write_fragment.show(getParentFragmentManager(), Post_Write_Fragment.TAG_POST_WRITE);
    }

    public void Get_Post() {
        adapter.Get_Post_Single();
        adapter.Get_Post_Child_Listener();
        adapter.notifyDataSetChanged();
    }
}