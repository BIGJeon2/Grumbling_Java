package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.adapter.Chatting_List_Rcv_Adapter;
import com.bigjeon.grumbling.data.Chat_User_Uid_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentChattingListViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("My_Chatting_List");
        reference.addChildEventListener(Chat_Child_Listener());
    }

    private ChildEventListener Chat_Child_Listener(){
        ChildEventListener Chat_Listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                 Chat_User_Uid_Data Chat_Room = snapshot.getValue(Chat_User_Uid_Data.class);
                 Chat_Room.setLast_Date(Change_Date(Chat_Room.getLast_Date()));
                 adapter.Add_List(0, Chat_Room);
                    Collections.sort(chat_list, new Comparator<Chat_User_Uid_Data>() {
                        @Override
                        public int compare(Chat_User_Uid_Data o1, Chat_User_Uid_Data o2) {
                            return o1.getLast_Date().compareTo(o2.getLast_Date());
                        }
                    });
                    Collections.reverse(chat_list);
                 adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Chat_User_Uid_Data Chat_Room = snapshot.getValue(Chat_User_Uid_Data.class);
                for (int i = 0; i < chat_list.size(); i++){
                    if (chat_list.get(i).getChat_Room_Id().equals(Chat_Room.getChat_Room_Id())){
                        chat_list.remove(i);
                        adapter.Add_List(0, Chat_Room);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        return Chat_Listener;
    }

    private String Change_Date(String write_date){
        String new_writedate = "0000";
        try{
            SimpleDateFormat before = new SimpleDateFormat("yyyy-MM-dd k:mm:ss:SSSS");
            SimpleDateFormat after = new SimpleDateFormat("MM-dd hh:mm");
            Date dt_format = before.parse(write_date);
            new_writedate = after.format(dt_format);
            return new_writedate;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return new_writedate;
    }

    @Override
    public void onStop() {
        super.onStop();
        chat_list.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("My_Chatting_List");
        reference.removeEventListener(Chat_Child_Listener());
    }


    @Override
    public void onPause() {
        super.onPause();
        chat_list.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("My_Chatting_List");
        reference.removeEventListener(Chat_Child_Listener());
    }

}