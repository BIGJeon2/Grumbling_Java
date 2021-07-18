package com.bigjeon.grumbling.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.data.User_Profile;
import com.bigjeon.grumbling.fragments.User_Profile_FriendsList_Fragment;
import com.bigjeon.grumbling.fragments.User_Profile_PostsList_Fragment;

import org.jetbrains.annotations.NotNull;

public class User_Profile_Frgment_Swipe_Adapter extends FragmentStateAdapter {
    private static final String TAG = "User_Post";

    public User_Profile_Frgment_Swipe_Adapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new User_Profile_PostsList_Fragment();
        else return new User_Profile_FriendsList_Fragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
