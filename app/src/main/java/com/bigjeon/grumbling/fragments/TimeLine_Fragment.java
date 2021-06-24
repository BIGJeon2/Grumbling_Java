package com.bigjeon.grumbling.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grumbling.R;
import com.example.grumbling.databinding.TimelineFragmentViewBinding;

public class TimeLine_Fragment extends Fragment {

    private TimelineFragmentViewBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.timeline_fragment_view_, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_post_view, container, false);

        return root;
    }

}