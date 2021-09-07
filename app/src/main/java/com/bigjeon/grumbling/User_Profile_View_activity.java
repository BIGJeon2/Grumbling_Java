package com.bigjeon.grumbling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bigjeon.grumbling.Model.Api;
import com.bigjeon.grumbling.Model.ApiCLient;
import com.bigjeon.grumbling.Model.Data;
import com.bigjeon.grumbling.Model.Model;
import com.bigjeon.grumbling.adapter.Fragment_Swipe_Adapter;
import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.adapter.User_Profile_Frgment_Swipe_Adapter;
import com.bigjeon.grumbling.data.Friend_Data;
import com.bigjeon.grumbling.data.Notification_Data;
import com.bigjeon.grumbling.data.Post_Data;
import com.bigjeon.grumbling.fragments.User_Profile_FriendsList_Fragment;
import com.bigjeon.grumbling.fragments.User_Profile_PostsList_Fragment;
import com.example.grumbling.R;
import com.example.grumbling.databinding.ActivityUserProfileViewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User_Profile_View_activity extends AppCompatActivity {

    private String My_Uid;
    private String My_Name;
    private String My_Img;
    public String User_Uid;
    public String User_Token;
    private DatabaseReference reference;
    private String Friend_State = "NONE";
    private String Notification_Key = "Add_Friend";
    private ActivityUserProfileViewBinding binding;
    private User_Profile_Frgment_Swipe_Adapter User_Profile_ViewPager_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile_view);
        binding.setShowUserProfileBinding(this);

        Get_My_Data();

        User_Profile_ViewPager_Adapter = new User_Profile_Frgment_Swipe_Adapter(this);
        binding.UserProfileViewPager2.setAdapter(User_Profile_ViewPager_Adapter);
        binding.UserProfileViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.UserProfileViewPager2.setCurrentItem(0, true);
        binding.UserProfileViewPager2.setOffscreenPageLimit(1);

        binding.UserProfileViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0){
                    binding.UserProfileViewPager2.setCurrentItem(position);
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Button_Background_Change(position);
            }
        });

        Intent intent = getIntent();
        if (intent.getStringExtra("UID") == null){
            User_Uid = intent.getStringExtra("USER_UID");
            Set_Users_Data();
            Get_User_Token();
        }else{
            User_Uid = intent.getStringExtra("UID");
            Set_Users_Data();
            Get_User_Token();
        }

        Check_My_Friend_State();

        binding.SettingFragmentChattingCiv.setOnClickListener(v -> Intent_To_P2P_Chatting());

        binding.SettingFragmentSendFriendRequestBtn.setOnClickListener(v -> Send_Friend_Request());
        binding.UserProfilePostCiv.setOnClickListener(v -> Change_Fragment_OnCLick(0));
        binding.UserProfileFriendCiv.setOnClickListener(v -> Change_Fragment_OnCLick(1));
        binding.UserProfileBackIMV.setOnClickListener(v -> onBackPressed());
    }

    private void Intent_To_P2P_Chatting() {
        Intent GO_P2P_Chat = new Intent(this, P2P_Chatting_Activity.class);
        GO_P2P_Chat.putExtra("USER_UID", User_Uid);
        GO_P2P_Chat.putExtra("MY_UID", My_Uid);
        startActivity(GO_P2P_Chat);
    }

    private void Change_Fragment_OnCLick(int i) {
        if (i == 0) {
            binding.UserProfileViewPager2.setCurrentItem(0, true);
        } else{
            binding.UserProfileViewPager2.setCurrentItem(1, true);
        }
    }

    private void Button_Background_Change(int position) {
        switch (position) {
            case 0:
                binding.UserProfilePostCiv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#90000000")));
                binding.UserProfileFriendCiv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#50000000")));
                break;
            case 1:
                binding.UserProfilePostCiv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#50000000")));
                binding.UserProfileFriendCiv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#90000000")));
                break;
        }
    }

    private void Set_Users_Data() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("UID", User_Uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        binding.SettingFragmentMyNameTv.setText(document.get("Name").toString());
                        Picasso.get().load(document.get("Img").toString()).into(binding.SettingFragmentMyProfileImgCiv);
                        break;
                    }
                }
            }
        });
    }

    private void Get_User_Token(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("Token");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User_Token = task.getResult().getValue().toString();
            }
        });
    }

    private void Get_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
    }

    private void Send_Noti_To_User(){
        Model model = new Model(User_Token, null, new Data(My_Name + "님이 회원님을 친구추가 하였습니다.", null, User_Uid + My_Uid, ".Friend", My_Uid, My_Img));
        Api apiService = ApiCLient.getClient().create(Api.class);
        retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(model);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("서버 통신!!", "성공" + User_Token);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("서버 통신!!", "실패");
            }
        });
    }

    private void Send_Friend_Request(){
        if(!Friend_State.equals("Friend")){
            SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String Send_Date = simpledate.format(date);
            //내 DB에 저장
            Friend_Data My_Friend = new Friend_Data(User_Uid, Send_Date);
            reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(User_Uid);
            reference.setValue(My_Friend);
            binding.SettingFragmentSendFriendRequestBtn.setBackgroundResource(R.drawable.round_shape);
            binding.SettingFragmentSendFriendRequestBtn.setText("친구");
            binding.SettingFragmentSendFriendRequestBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent_Green));
            binding.SettingFragmentChattingCiv.setVisibility(View.VISIBLE);
            binding.P2PChatTV.setVisibility(View.VISIBLE);
            Send_Noti_To_User();

            Notification_Data Noti = new Notification_Data(Notification_Key, My_Uid, Send_Date, "None");
            reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("Notifications").child("Firend_Timepeed").child(Notification_Key + My_Uid);
            reference.setValue(Noti);
        }
    }

    private void Check_My_Friend_State(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(User_Uid);
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Friend_State = "NONE";
                    binding.SettingFragmentChattingCiv.setVisibility(View.GONE);
                    binding.P2PChatTV.setVisibility(View.GONE);
                }else{
                    Friend_Data Friends = task.getResult().getValue(Friend_Data.class);
                    if (Friends == null){
                        Friend_State = "NONE";
                        binding.SettingFragmentChattingCiv.setVisibility(View.GONE);
                        binding.P2PChatTV.setVisibility(View.GONE);
                    }else if (Friends.getUid().equals(User_Uid)){
                        Friend_State = "Friend";
                        binding.SettingFragmentSendFriendRequestBtn.setBackgroundResource(R.drawable.round_shape);
                        binding.SettingFragmentSendFriendRequestBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent_Green));
                        binding.SettingFragmentSendFriendRequestBtn.setText("친구");
                    }
                }
            }
        });
    }

    public String SendData(){
        return User_Uid;
    }
    
}