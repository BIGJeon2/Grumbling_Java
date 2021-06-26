package com.bigjeon.grumbling.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.bigjeon.grumbling.fragments.Setting_Fragment;
import com.bigjeon.grumbling.fragments.TimeLine_Fragment;

import org.jetbrains.annotations.NotNull;

public class Fragment_Swipe_Adapter extends FragmentStateAdapter {

    public Fragment_Swipe_Adapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 0) return new TimeLine_Fragment();
        else if (position == 1) return new Post_View_Fragment();
        else return new Setting_Fragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
