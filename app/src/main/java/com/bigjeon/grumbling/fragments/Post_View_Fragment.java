package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentPostViewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Post_View_Fragment extends Fragment{
    private static final String TAG = "My_Check";
    private FragmentPostViewBinding binding;
    private FirebaseFirestore DB;
    private Post_View_Rcv_Adapter adapter;
    ArrayList<Post_Data> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_view, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_post_view, container, false);

        DB = FirebaseFirestore.getInstance();

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

        binding.PostWritePostStartCircleImgBtn.setOnClickListener( V -> Alert_Post_Write_Dialog());
        return root;
    }

    private void Alert_Post_Write_Dialog() {
        Post_Write_Fragment post_write_fragment = Post_Write_Fragment.getInstance();
        post_write_fragment.show(getParentFragmentManager(), Post_Write_Fragment.TAG_POST_WRITE);
    }

    private void Get_Post(){
        DB.collection("Posts")
                .whereEqualTo("grade", "모든 사용자")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Post_Data post = document.toObject(Post_Data.class);
                                list.add(0, post);
                                Log.d(TAG, "Uri = "+ post.getPost_Background());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        }
}