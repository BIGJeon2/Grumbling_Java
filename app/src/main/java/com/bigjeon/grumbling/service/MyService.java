package com.bigjeon.grumbling.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
        String My_Uid = My_Data.getString("UID", null);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("State");
        reference.setValue("NONE");
        stopSelf();
        super.onDestroy();
    }
}
