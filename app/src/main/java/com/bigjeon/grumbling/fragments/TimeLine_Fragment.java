package com.bigjeon.grumbling.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.adapter.Friend_List_Adapter;
import com.bigjeon.grumbling.data.Friends_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.TimelineFragmentViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TimeLine_Fragment extends Fragment {

    private TimelineFragmentViewBinding binding;
    private DatabaseReference reference;
    private String My_Uid;
    private Friend_List_Adapter Friend_Adapter, Request_Adapter;
    private ArrayList<Friends_Data> Request_List = new ArrayList<>();
    private ArrayList<String> Friends_List = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.timeline_fragment_view_, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_post_view, container, false);

        My_Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true);
        Request_Adapter = new Friend_List_Adapter(getContext(), Request_List);
        binding.RequestFriendListRcv.setAdapter(Request_Adapter);
        binding.RequestFriendListRcv.setLayoutManager(lm);
        binding.RequestFriendListRcv.setHasFixedSize(true);
        binding.RequestFriendListRcv.setNestedScrollingEnabled(false);
        binding.RequestFriendListRcv.scrollToPosition(0);
        Get_Friend_List();

        return root;
    }

    private void Get_Friend_List(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Receive_Friends_Request");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Friends_Data request = data.getValue(Friends_Data.class);
                    if (request.getState().equals("ING")){
                        Request_List.add(request);
                        Request_Adapter.notifyDataSetChanged();
                    }else if (request.getState().equals("ACCEPT")){
                        Friends_List.add(request.getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}