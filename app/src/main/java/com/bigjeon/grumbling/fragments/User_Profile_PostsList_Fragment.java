package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentUserProfilePostsListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class User_Profile_PostsList_Fragment extends Fragment {
    private static final String TAG = "User_Post_Fragment";
    private User_Profile_View_activity Parent_Act;
    private FragmentUserProfilePostsListBinding binding;
    private String User_Uid = "?";
    private String My_Name;
    private DatabaseReference reference;
    private String Get_Post_Key = "유저 게시글";
    private ArrayList<Post_Data> list = new ArrayList<>();
    private Post_View_Rcv_Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user__profile__posts_list_, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_user__profile__posts_list_, container, false);

        Parent_Act = (User_Profile_View_activity)getActivity();
        User_Uid = Parent_Act.SendData();

        SharedPreferences Get_My_Data = this.getActivity().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Name = Get_My_Data.getString("NAME", null);

        RecyclerView rcv = binding.UserPostListFragmentRCV;
        adapter = new Post_View_Rcv_Adapter(getContext(), list, Get_Post_Key, My_Name);
        LinearLayoutManager lm = new LinearLayoutManager(v.getContext());
        rcv.setLayoutManager(lm);
        rcv.setAdapter(adapter);
        rcv.setHasFixedSize(true);
        Get_Users_Posts();

        return root;
    }

    private void Get_Users_Posts() {
        Get_Post();
        adapter.Get_Post_Child_Listener();
        adapter.notifyDataSetChanged();
    }

    /*
    유저 포스트중 모든 보안이 모든 사용자일 경우만 보여줌
     */
    private void Get_Post(){
        reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Post_Data post = data.getValue(Post_Data.class);
                    if (post.getUser_Uid().equals(User_Uid) && post.getGrade().equals("모든 사용자")){
                        list.add(0, post);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


}