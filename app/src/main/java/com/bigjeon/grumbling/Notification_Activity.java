package com.bigjeon.grumbling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import com.bigjeon.grumbling.adapter.Timepeed_ViewPager2_Adapter;
import com.example.grumbling.Notification_Binding;
import com.example.grumbling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Notification_Activity extends AppCompatActivity {

    private Notification_Binding binding;
    private DatabaseReference reference;
    private String My_Uid;
    private Timepeed_ViewPager2_Adapter ViewPager_Adapter;
    private String My_Img;
    private String My_Name;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        binding.setNotificationActivity(this);

        Get_My_Profile();

        ViewPager_Adapter = new Timepeed_ViewPager2_Adapter(this);
        binding.TimePeedViewPager2.setAdapter(ViewPager_Adapter);
        binding.TimePeedViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.TimePeedViewPager2.setOffscreenPageLimit(1);

        binding.TimePeedViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0){
                    binding.TimePeedViewPager2.setCurrentItem(position);
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Button_Background_Change(position);
            }
        });

        binding.TimepeedTVPost.setOnClickListener(v -> Change_Fragment_OnCLick(0));
        binding.TimepeedTVFriends.setOnClickListener(v -> Change_Fragment_OnCLick(1));
        binding.NotificationBackIMV.setOnClickListener(v -> onBackPressed());
    }

    private void Get_My_Profile(){
        SharedPreferences Get_My_Data = this.getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = Get_My_Data.getString("UID", null);
        My_Name = Get_My_Data.getString("NAME", null);
        My_Img = Get_My_Data.getString("IMG", null);
    }

    private void Change_Fragment_OnCLick(int i) {
        if (i == 0){
            binding.TimePeedViewPager2.setCurrentItem(0, true);
        }else if (i == 1){
            binding.TimePeedViewPager2.setCurrentItem(1, true);
        }
    }

    private void Button_Background_Change(int position){
        switch (position){
            case 0 :
                binding.TimepeedTVPost.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFBB86FC")));
                binding.TimepeedTVFriends.setTextColor(ColorStateList.valueOf(Color.GRAY));
                break;
            case 1 :
                binding.TimepeedTVPost.setTextColor(ColorStateList.valueOf(Color.GRAY));
                binding.TimepeedTVFriends.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFBB86FC")));
                break;
        }
    }

}