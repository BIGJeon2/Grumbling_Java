package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.Setting_My_Profile_Activity;
import com.bigjeon.grumbling.adapter.Friend_List_Adapter;
import com.bigjeon.grumbling.data.Friend_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FriendListFragmentViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Friend_List_Fragment extends Fragment {

    private FriendListFragmentViewBinding binding;
    private DatabaseReference reference;
    private Context context = getContext();
    private String My_Uid;
    private String My_Img;
    private String My_Name;
    private String My_Location;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Friend_List_Adapter Friend_Adapter;
    private ArrayList<Friend_Data> Friends_List = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.friend_list_fragment_view_, container, false);
        View root = binding.getRoot();

        Get_My_Profile();
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        Friend_Adapter = new Friend_List_Adapter(getContext(), My_Uid, Friends_List, null, My_Uid);
        binding.FriendListRcv.setAdapter(Friend_Adapter);
        binding.FriendListRcv.setLayoutManager(lm);
        binding.FriendListRcv.setHasFixedSize(true);
        binding.FriendListRcv.setNestedScrollingEnabled(false);

        binding.FriendListMyProfileContainer.setOnClickListener(v -> Go_User_Profile_View_Act());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Friends_List.clear();
        Get_Friend_List();
    }

    private void Go_User_Profile_View_Act() {
            Intent Go_View_My_Profile_Intent = new Intent(getContext(), Setting_My_Profile_Activity.class);
            Go_View_My_Profile_Intent.putExtra("UID", My_Uid);
            getContext().startActivity(Go_View_My_Profile_Intent);
    }

    private void Get_My_Profile(){
        SharedPreferences Get_My_Data = this.getActivity().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = Get_My_Data.getString("UID", null);
        My_Name = Get_My_Data.getString("NAME", null);
        My_Img = Get_My_Data.getString("IMG", null);
        My_Location = Get_My_Data.getString("LOCATION", null);

        Picasso.get().load(My_Img).into(binding.FriendListMyImgCiv);
        binding.FriendListMyNameTV.setText(My_Name);
        binding.FriendListFragmentMyUid.setText("#" + My_Location);
    }

    private void Get_Friend_List(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Friend_Data friend = data.getValue(Friend_Data.class);
                    Friends_List.add(friend);
                }
                Friend_Adapter.notifyDataSetChanged();
                binding.TV2.setText("- 친구 " + Friend_Adapter.getItemCount());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}