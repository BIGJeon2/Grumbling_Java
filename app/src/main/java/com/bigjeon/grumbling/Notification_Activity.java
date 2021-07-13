package com.bigjeon.grumbling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bigjeon.grumbling.adapter.Friend_List_Adapter;
import com.bigjeon.grumbling.adapter.Request_Friend_List_Adapter;
import com.bigjeon.grumbling.data.Friend_Data;
import com.bigjeon.grumbling.data.Request_Friends_Data;
import com.example.grumbling.Notification_Binding;
import com.example.grumbling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Notification_Activity extends AppCompatActivity {

    private Notification_Binding binding;
    private DatabaseReference reference;
    private String My_Uid;
    private String My_Img;
    private String My_Name;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Request_Friend_List_Adapter Request_Adapter;
    private ArrayList<Request_Friends_Data> Request_List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        binding.setNotificationActivity(this);

        Get_My_Profile();
        Get_Request_Friend_List();
    }

    private void Get_My_Profile(){
        SharedPreferences Get_My_Data = this.getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = Get_My_Data.getString("UID", null);
        My_Name = Get_My_Data.getString("NAME", null);
        My_Img = Get_My_Data.getString("IMG", null);
    }

    private void Get_Request_Friend_List(){
        LinearLayoutManager lm_request = new LinearLayoutManager(this);
        lm_request.setStackFromEnd(true);
        Request_Adapter = new Request_Friend_List_Adapter(this, Request_List);
        binding.NotiRequestFriendListRcv.setAdapter(Request_Adapter);
        binding.NotiRequestFriendListRcv.setLayoutManager(lm_request);
        binding.NotiRequestFriendListRcv.setHasFixedSize(true);
        binding.NotiRequestFriendListRcv.setNestedScrollingEnabled(false);
        binding.NotiRequestFriendListRcv.scrollToPosition(0);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Receive_Friends_Request");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Request_Friends_Data request = data.getValue(Request_Friends_Data.class);
                    if (request.getState().equals("ING")){
                        Request_List.add(request);
                        Request_Adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}