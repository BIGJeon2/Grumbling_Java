package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.adapter.Friend_List_Adapter;
import com.bigjeon.grumbling.data.Friend_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentUserProfileFriendsListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class User_Profile_FriendsList_Fragment extends Fragment {

    private FragmentUserProfileFriendsListBinding binding;
    private String User_Uid;
    private String My_Uid;
    private User_Profile_View_activity Parent_Act;
    private Friend_List_Adapter Friend_Adapter;
    private ArrayList<Friend_Data> Friends_List = new ArrayList<>();
    private ArrayList<Friend_Data> My_Freiend_List = new ArrayList<>();
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user__profile__friends_list_, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_user__profile__friends_list_, container, false);

        Parent_Act = (User_Profile_View_activity)getActivity();
        User_Uid = Parent_Act.SendData();
        SharedPreferences Get_My_Data = this.getActivity().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = Get_My_Data.getString("UID", null);
        Get_My_FirendList();

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        Friend_Adapter = new Friend_List_Adapter(getContext(), User_Uid, Friends_List, My_Freiend_List, My_Uid);
        binding.UserFriendsListFragmentRCV.setAdapter(Friend_Adapter);
        binding.UserFriendsListFragmentRCV.setLayoutManager(lm);
        binding.UserFriendsListFragmentRCV.setHasFixedSize(true);
        binding.UserFriendsListFragmentRCV.setNestedScrollingEnabled(false);

        return root;
    }
    private void Get_Friend_List(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("Friends");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Friend_Data friend = data.getValue(Friend_Data.class);
                    Friends_List.add(friend);
                }
                Friend_Adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Friends_List.clear();
        Get_Friend_List();
    }

    private void Get_My_FirendList(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Friend_Data friend = data.getValue(Friend_Data.class);
                    My_Freiend_List.add(friend);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}