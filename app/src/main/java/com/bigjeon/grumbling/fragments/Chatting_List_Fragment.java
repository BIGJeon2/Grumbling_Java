package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentChattingListViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Chatting_List_Fragment extends Fragment {

    public static Context mcontext;
    private String My_Uid;
    private FragmentChattingListViewBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference DB;
    private Post_View_Rcv_Adapter adapter;
    private String Get_Content_Grade = "좋아요 게시글";
    ArrayList<Post_Data> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatting_list_view, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_chatting_list_view, container, false);

        mcontext = this.getContext();
        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference("Chats");

        My_Uid = mAuth.getCurrentUser().getUid();

        return root;
    }

    private void Get_Chatting(){
        DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for(DataSnapshot )
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}