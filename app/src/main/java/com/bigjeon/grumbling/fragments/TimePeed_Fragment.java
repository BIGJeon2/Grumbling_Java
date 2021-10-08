package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentTimePeedBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class TimePeed_Fragment extends Fragment {
    private FragmentTimePeedBinding binding;
    private DatabaseReference reference;
    private Context mcontext;
    private String My_Uid;
    private String My_Img;
    private String My_Name;
    private Fragment Favorite_Fragmet;
    private Fragment Friend_Fragment;
    private FragmentTransaction transaction;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_peed_, container, false);
        View root = binding.getRoot();

        mcontext = getContext().getApplicationContext();
        Friend_Fragment = new Fragment_TimePeed_Friends();
        Favorite_Fragmet = new Fragment_TimePeed_Post();
        Get_My_Profile();

        Favorite_Fragmet = new Fragment_TimePeed_Post();
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.Time_Peed_Activity_FrameLayout, Favorite_Fragmet).commit();

        binding.TimepeedPostBtnContainer.setOnClickListener(v -> Fragment_Change(0));
        binding.TimepeedFriendBtnContainer.setOnClickListener(v -> Fragment_Change(1));

        return root;
    }

    private void Get_My_Profile(){
        SharedPreferences Get_My_Data = this.getActivity().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = Get_My_Data.getString("UID", null);
        My_Name = Get_My_Data.getString("NAME", null);
        My_Img = Get_My_Data.getString("IMG", null);
    }

    private void Fragment_Change(int position){
        switch (position){
            case 0 :
                transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.Time_Peed_Activity_FrameLayout, Favorite_Fragmet).commit();
                binding.TimepeedTVPost.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                binding.TimepeedPostTv.setTextColor(ContextCompat.getColor(mcontext, R.color.Theme_Text_Color));
                binding.TimepeedFriendsTv.setTextColor(ContextCompat.getColor(mcontext, R.color.Theme_Less_Accent_Color));
                binding.TimepeedTVPost.setBackgroundTintList(ContextCompat.getColorStateList(mcontext, R.color.light_pink));
                binding.TimepeedTVFriends.setBackgroundTintList(ContextCompat.getColorStateList(mcontext, R.color.Btn_Off_Color));
                break;
            case 1 :
                transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.Time_Peed_Activity_FrameLayout, Friend_Fragment).commit();
                binding.TimepeedTVPost.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                binding.TimepeedPostTv.setTextColor(ContextCompat.getColor(mcontext, R.color.Theme_Less_Accent_Color));
                binding.TimepeedFriendsTv.setTextColor(ContextCompat.getColor(mcontext, R.color.Theme_Text_Color));
                binding.TimepeedTVPost.setBackgroundTintList(ContextCompat.getColorStateList(mcontext, R.color.Btn_Off_Color));
                binding.TimepeedTVFriends.setBackgroundTintList(ContextCompat.getColorStateList(mcontext, R.color.Btn_On_Color));
                break;
        }
    }
}