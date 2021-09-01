package com.bigjeon.grumbling.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bigjeon.grumbling.fragments.Fragment_TimePeed_Friends;
import com.bigjeon.grumbling.fragments.Frament_TimePeed_Post;

public class Timepeed_ViewPager2_Adapter extends FragmentStateAdapter {
    public Timepeed_ViewPager2_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new Frament_TimePeed_Post();
        else return new Fragment_TimePeed_Friends();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
