package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bigjeon.grumbling.App_Main_Activity;
import com.bigjeon.grumbling.Post_Write_Activity;
import com.bigjeon.grumbling.adapter.Category_OnClick;
import com.bigjeon.grumbling.adapter.Category_Rcv_Adapter;
import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.App_Main_Binding;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentPostViewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Post_View_Fragment extends Fragment {
    public static Context mcontext;
    private String My_Uid;
    private FragmentPostViewBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference DB;
    private String My_Name;
    private Post_View_Rcv_Adapter adapter;
    private int Category_State = 1;
    private String Get_Content_Grade = "모든 게시글";
    ArrayList<Post_Data> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_view, container, false);
        View root = binding.getRoot();

        mcontext = this.getContext();
        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference("Posts");

        SharedPreferences Get_My_Data = this.getActivity().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Name = Get_My_Data.getString("NAME", null);

        My_Uid = mAuth.getCurrentUser().getUid();

        RecyclerView rcv = binding.PostRecyclerView;
        adapter = new Post_View_Rcv_Adapter(mcontext, list, Get_Content_Grade, My_Name, null);
        LinearLayoutManager lm = new LinearLayoutManager(mcontext);
        rcv.setLayoutManager(lm);
        rcv.setAdapter(adapter);
        rcv.setHasFixedSize(true);
        Get_Post();

        binding.PostSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                adapter.notifyDataSetChanged();
                Get_Post();
                binding.PostSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }

    public void Get_Post() {
        adapter.Get_Post_Single();
        adapter.Get_Post_Child_Listener();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.Remove_Post_Child_Listener();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.Get_Post_Child_Listener();
    }

//    private void Category_Color_Change_On(int Select_Btn){
//        switch (Select_Btn){
//            case 1 : binding.CategoryTVAll.setBackgroundResource(R.drawable.round_shape);
//                binding.CategoryTVAll.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Green));
//                Category_Color_Change_OFF(Category_State);
//                Category_State = 1;
//                Get_Content_Grade = "모든 게시글";
//
//                break;
//            case 2 : binding.CategoryTVTalk.setBackgroundResource(R.drawable.round_shape);
//                binding.CategoryTVTalk.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Green));
//                Category_Color_Change_OFF(Category_State);
//                Category_State = 2;
//                Get_Content_Grade = "잡담";
//                break;
//            case 3 : binding.CategoryTVMeet.setBackgroundResource(R.drawable.round_shape);
//                binding.CategoryTVMeet.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Green));
//                Category_Color_Change_OFF(Category_State);
//                Category_State = 3;
//                Get_Content_Grade = "모임";
//                break;
//            case 4 : binding.CategoryTVGame.setBackgroundResource(R.drawable.round_shape);
//                binding.CategoryTVGame.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Green));
//                Category_Color_Change_OFF(Category_State);
//                Category_State = 4;
//                Get_Content_Grade = "게임";
//                break;
//            case 5 : binding.CategoryTVPride.setBackgroundResource(R.drawable.round_shape);
//                binding.CategoryTVPride.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Green));
//                Category_Color_Change_OFF(Category_State);
//                Category_State = 5;
//                Get_Content_Grade = "자랑";
//                break;
//            case 6 : binding.CategoryTVGrung.setBackgroundResource(R.drawable.round_shape);
//                binding.CategoryTVGrung.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Green));
//                Category_Color_Change_OFF(Category_State);
//                Category_State = 6;
//                Get_Content_Grade = "고민";
//                break;
//        }
//        adapter.Set_Grade(Get_Content_Grade);
//        Get_Post();
//    }

    /*private void Category_Color_Change_OFF(int State){
        switch (State){
            case 1 : binding.CategoryTVAll.setBackgroundResource(R.drawable.round_shape_off);
                binding.CategoryTVAll.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Black50));
                break;
            case 2 : binding.CategoryTVTalk.setBackgroundResource(R.drawable.round_shape_off);
                binding.CategoryTVTalk.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Black50));
                break;
            case 3 : binding.CategoryTVMeet.setBackgroundResource(R.drawable.round_shape_off);
                binding.CategoryTVMeet.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Black50));
                break;
            case 4 : binding.CategoryTVGame.setBackgroundResource(R.drawable.round_shape_off);
                binding.CategoryTVGame.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Black50));
                break;
            case 5 : binding.CategoryTVPride.setBackgroundResource(R.drawable.round_shape_off);
                binding.CategoryTVPride.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Black50));
                break;
            case 6 : binding.CategoryTVGrung.setBackgroundResource(R.drawable.round_shape_off);
                binding.CategoryTVGrung.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.Transparent_Black50));
                break;
        }
    }*/


}