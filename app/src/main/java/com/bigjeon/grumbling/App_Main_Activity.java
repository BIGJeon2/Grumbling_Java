package com.bigjeon.grumbling;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bigjeon.grumbling.adapter.Fragment_Swipe_Adapter;
import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.bigjeon.grumbling.service.MyService;
import com.example.grumbling.App_Main_Binding;
import com.example.grumbling.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

public class App_Main_Activity extends AppCompatActivity implements View.OnCreateContextMenuListener{
    public static Context mcontext;
    private App_Main_Binding binding;
    public String My_Uid;
    public String My_Img;
    public String My_Name;
    public String My_Email;
    public String My_Token;
    public String Get_Noti_State;
    public String Get_Noti_Data;
    private DatabaseReference reference;
    private FragmentStateAdapter ViewPager_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_main);
        binding.setAppMainActivity(this);

        startService(new Intent(this, MyService.class));

        mcontext = this;
        Set_My_Data();

        //토큰 가져오기
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(mcontext, "토큰 불러오기 실패", Toast.LENGTH_SHORT).show();
                    return;
                }
                My_Token = task.getResult();
                reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Token");
                reference.setValue(My_Token);
            }
        });

        ViewPager_Adapter = new Fragment_Swipe_Adapter(this);
        binding.AppMainViewPager2.setAdapter(ViewPager_Adapter);
        binding.AppMainViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.AppMainViewPager2.setCurrentItem(1, false);
        binding.AppMainViewPager2.setOffscreenPageLimit(3);

        binding.AppMainViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0){
                    binding.AppMainViewPager2.setCurrentItem(position);
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Button_Background_Change(position);
            }
        });
        binding.AppMainFriendCiv.setOnClickListener(v -> Change_Fragment_OnCLick(0));
        binding.AppMainPostCiv.setOnClickListener(v -> Change_Fragment_OnCLick(1));
        binding.AppMainChattingCiv.setOnClickListener(v -> Change_Fragment_OnCLick(2));
        binding.AppMainNoticeCiv.setOnClickListener(v -> Go_Notification_Activity());
        binding.AppMainWritePostCIV.setOnClickListener(v -> Go_Post_Write_Act());
    }

    private void Change_Fragment_OnCLick(int i) {
        if (i == 0){
            binding.AppMainViewPager2.setCurrentItem(0, true);
        }else if (i == 1){
            binding.AppMainViewPager2.setCurrentItem(1, true);
        }else{
            binding.AppMainViewPager2.setCurrentItem(2, true);
        }
    }

    private void Go_Notification_Activity() {
        Intent Go_Notifi = new Intent(this, Notification_Activity.class);
        startActivity(Go_Notifi);
    }

    public void Set_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
        My_Email = My_Data.getString("EMAIL", null);
    }

    private void Button_Background_Change(int position){
        switch (position){
            case 0 :
                binding.AppMainFriendCiv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFBB86FC")));
                binding.AppMainPostCiv.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                binding.AppMainChattingCiv.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case 1 :
                binding.AppMainFriendCiv.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                binding.AppMainPostCiv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFBB86FC")));
                binding.AppMainChattingCiv.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case 2 :
                binding.AppMainFriendCiv.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                binding.AppMainPostCiv.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                binding.AppMainChattingCiv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFBB86FC")));
                break;
        }
    }

    private void Go_Post_Write_Act() {
        Intent Go_Post_Write = new Intent(this, Post_Write_Activity.class);
        Go_Post_Write.putExtra("KEY", "CREATE");
        Go_Post_Write.putExtra("TITLE", "NONE");
        startActivity(Go_Post_Write);
    }

}