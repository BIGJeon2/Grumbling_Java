package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bigjeon.grumbling.adapter.Timepeed_Rcv_Friends_Adapter;
import com.bigjeon.grumbling.data.Notification_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentTimepeedFriendsBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Fragment_TimePeed_Friends extends Fragment {
    private FragmentTimepeedFriendsBinding binding;
    private DatabaseReference reference;
    private String My_Uid;
    private String My_Img;
    private String My_Name;
    private Timepeed_Rcv_Friends_Adapter adapter;
    private ArrayList<Notification_Data> TimePeed_Friends_List = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Get_My_Profile();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Notifications").child("Firend_Timepeed");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timepeed_friends, container, false);
        View root = binding.getRoot();
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        adapter = new Timepeed_Rcv_Friends_Adapter(getContext(), TimePeed_Friends_List, My_Uid);
        binding.TimepeedFriendsRCV.setAdapter(adapter);
        binding.TimepeedFriendsRCV.setLayoutManager(lm);
        binding.TimepeedFriendsRCV.setHasFixedSize(true);
        binding.TimepeedFriendsRCV.setNestedScrollingEnabled(false);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        reference.addChildEventListener(Set_ChildEvent_Listner_Get_TimePeed());
    }

    @Override
    public void onPause() {
        super.onPause();
        TimePeed_Friends_List.clear();
        reference.removeEventListener(Set_ChildEvent_Listner_Get_TimePeed());
    }

    private void Get_My_Profile(){
        SharedPreferences Get_My_Data = this.getActivity().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = Get_My_Data.getString("UID", null);
        My_Name = Get_My_Data.getString("NAME", null);
        My_Img = Get_My_Data.getString("IMG", null);
    }

    public ChildEventListener Set_ChildEvent_Listner_Get_TimePeed(){

        ChildEventListener listner = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notification_Data data = snapshot.getValue(Notification_Data.class);
                TimePeed_Friends_List.add(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notification_Data data = snapshot.getValue(Notification_Data.class);
                for (int i = 0; i < TimePeed_Friends_List.size(); i++) {
                    if (TimePeed_Friends_List.get(i).getPost_Title().equals(data.getPost_Title())) {
                        TimePeed_Friends_List.set(i, data);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Notification_Data data = snapshot.getValue(Notification_Data.class);
                for (int i = 0; i < TimePeed_Friends_List.size(); i++) {
                    if (TimePeed_Friends_List.get(i).getPost_Title().equals(data.getPost_Title())) {
                        TimePeed_Friends_List.remove(data);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        return listner;
    }
}