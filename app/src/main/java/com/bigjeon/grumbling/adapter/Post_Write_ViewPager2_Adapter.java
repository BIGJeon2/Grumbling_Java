package com.bigjeon.grumbling.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bigjeon.grumbling.fragments.Set_Post_Background_Fragment;
import com.bigjeon.grumbling.fragments.Set_Post_Text_Fragment;
import com.bigjeon.grumbling.fragments.User_Profile_FriendsList_Fragment;
import com.bigjeon.grumbling.fragments.User_Profile_PostsList_Fragment;

import org.jetbrains.annotations.NotNull;

public class Post_Write_ViewPager2_Adapter extends FragmentStateAdapter {

    public Post_Write_ViewPager2_Adapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new Set_Post_Background_Fragment();
        else return new Set_Post_Text_Fragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
