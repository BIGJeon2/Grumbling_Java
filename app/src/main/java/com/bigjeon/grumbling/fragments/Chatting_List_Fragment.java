package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigjeon.grumbling.adapter.Request_Friend_List_Adapter;
import com.bigjeon.grumbling.data.Request_Friends_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentChattingListViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Chatting_List_Fragment extends Fragment {

    public static Context mcontext;
    private FragmentChattingListViewBinding binding;
    private DatabaseReference reference;
    private ArrayList<Request_Friends_Data> Request_List = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatting_list_view, container, false);
        View root = binding.getRoot();
        View v = inflater.inflate(R.layout.fragment_chatting_list_view, container, false);

        return root;
    }


}