package com.bigjeon.grumbling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bigjeon.grumbling.adapter.Notification_List_Adapter;
import com.bigjeon.grumbling.data.Notification_Data;
import com.example.grumbling.Notification_Binding;
import com.example.grumbling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Notification_Activity extends AppCompatActivity {

    private Notification_Binding binding;
    private DatabaseReference reference;
    private String My_Uid;
    private String My_Img;
    private String My_Name;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Notification_List_Adapter Notification_Adapter;
    private ArrayList<Notification_Data> Notification_List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        binding.setNotificationActivity(this);

        Get_My_Profile();

        LinearLayoutManager lm_request = new LinearLayoutManager(this);
        Notification_Adapter = new Notification_List_Adapter(this, Notification_List);
        binding.NotiRequestFriendListRcv.setAdapter(Notification_Adapter);
        binding.NotiRequestFriendListRcv.setLayoutManager(lm_request);
        binding.NotiRequestFriendListRcv.setHasFixedSize(true);
        binding.NotiRequestFriendListRcv.setNestedScrollingEnabled(false);
        Get_All_Notification();

        binding.NotificationSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Notification_List.clear();
                Get_All_Notification();
                binding.NotificationSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void Get_My_Profile(){
        SharedPreferences Get_My_Data = this.getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = Get_My_Data.getString("UID", null);
        My_Name = Get_My_Data.getString("NAME", null);
        My_Img = Get_My_Data.getString("IMG", null);
    }

    private void Get_All_Notification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Notifications");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notification_Data Noti = data.getValue(Notification_Data.class);
                    if (!Noti.getUid().equals(My_Uid)){
                        Notification_List.add(Noti);
                        Notification_Adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}