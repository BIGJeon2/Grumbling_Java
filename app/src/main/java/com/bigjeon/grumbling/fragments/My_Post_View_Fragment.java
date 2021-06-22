package com.bigjeon.grumbling.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentMyPostViewBinding;
import com.example.grumbling.databinding.FragmentPostViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class My_Post_View_Fragment extends Fragment {

    private static final String TAG = "My_Check";
    private FragmentMyPostViewBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference DB;
    private Post_View_Rcv_Adapter adapter;
    ArrayList<Post_Data> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my__post__view_, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_post_view, container, false);

        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference("Posts");

        RecyclerView rcv = binding.PostRecyclerView;
        adapter = new Post_View_Rcv_Adapter(list);
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

    private void Get_Post() {
        String My_Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    Post_Data post = data.getValue(Post_Data.class);
                    if (post.getUser_Uid().equals(My_Uid))list.add(0, post);
                    Log.d(TAG, "Uri = "+ post.getPost_Background());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}