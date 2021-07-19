package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.adapter.Chatting_List_Rcv_Adapter;
import com.bigjeon.grumbling.adapter.Notification_List_Adapter;
import com.bigjeon.grumbling.data.Chat_User_Uid_Data;
import com.bigjeon.grumbling.data.Notification_Data;
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
    private String My_Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FragmentChattingListViewBinding binding;
    private Chatting_List_Rcv_Adapter adapter;
    private ArrayList<Chat_User_Uid_Data> chat_list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatting_list_view, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_chatting_list_view, container, false);

        mcontext = getContext();

        LinearLayoutManager lm_request = new LinearLayoutManager(mcontext);
        adapter = new Chatting_List_Rcv_Adapter(mcontext, My_Uid, chat_list);
        binding.ChattingListRcv.setAdapter(adapter);
        binding.ChattingListRcv.setLayoutManager(lm_request);
        binding.ChattingListRcv.setHasFixedSize(true);
        binding.ChattingListRcv.setNestedScrollingEnabled(false);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        chat_list.clear();
        Get_Chatting_Room();
    }

    private void Get_Chatting_Room(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("My_Chatting_List");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chat_list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Chat_User_Uid_Data chat = data.getValue(Chat_User_Uid_Data.class);
                    chat_list.add(chat);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}