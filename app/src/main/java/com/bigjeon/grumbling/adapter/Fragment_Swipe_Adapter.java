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

    public int mCount;

    public Fragment_Swipe_Adapter(@NonNull @NotNull FragmentActivity fa, int count) {
        super(fa);
        this.mCount = count;
    }


    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        if (index == 0) return new TimeLine_Fragment();
        else if (index == 1) return new Post_View_Fragment();
        else return new Setting_Fragment();
    }

    private int getRealPosition(int position){
        return position % mCount;
    }

    @Override
    public int getItemCount() {
        return 200;
    }
}
