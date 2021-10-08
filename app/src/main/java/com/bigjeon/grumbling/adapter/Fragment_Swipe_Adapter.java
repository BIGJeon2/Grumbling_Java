package com.bigjeon.grumbling.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bigjeon.grumbling.fragments.Chatting_List_Fragment;
import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.bigjeon.grumbling.fragments.Friend_List_Fragment;
import com.bigjeon.grumbling.fragments.TimePeed_Fragment;

import org.jetbrains.annotations.NotNull;

public class Fragment_Swipe_Adapter extends FragmentStateAdapter {

    public Fragment_Swipe_Adapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 0) return new Friend_List_Fragment();
        else if (position == 1) return new Chatting_List_Fragment();
        else if (position == 2) return new Post_View_Fragment();
        else return new TimePeed_Fragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
