package com.bigjeon.grumbling.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bigjeon.grumbling.Google_Login_Activity;
import com.bigjeon.grumbling.MainActivity;
import com.bigjeon.grumbling.Set_User_Profile_Activity;
import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentSettingBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Setting_Fragment extends Fragment {

    private FirebaseAuth mAuth;
    private String My_Uid;
    private String My_Name;
    private String My_Img;
    private String My_Email;
    private DatabaseReference DB;
    private String Get_Post_Key = "나의 게시글";
    private FragmentSettingBinding binding;
    private Post_View_Rcv_Adapter adapter;
    private ArrayList<Post_Data> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_, container, false);
        View root = binding.getRoot();

        Set_My_Data();
        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference("Posts");

        RecyclerView rcv = binding.SettingFragmnetMyPostsRCV;
        adapter = new Post_View_Rcv_Adapter(getContext(), list, Get_Post_Key);
        LinearLayoutManager lm = new LinearLayoutManager(root.getContext());
        rcv.setLayoutManager(lm);
        rcv.setAdapter(adapter);
        rcv.setHasFixedSize(true);

        Get_My_Posts();

        binding.SettingFragmentSignOutBtn.setOnClickListener(v -> Sign_Out());
        binding.SettingFragmentChangeProfileBtn.setOnClickListener(v -> Go_Profile_Set_Act());
        return root;
    }

    private void Go_Profile_Set_Act() {
        Intent Set_Profile_Intent = new Intent(getActivity(), Set_User_Profile_Activity.class);
        Set_Profile_Intent.putExtra("UID", My_Uid);
        Set_Profile_Intent.putExtra("CODE", "CHANGE_SET");
        Set_Profile_Intent.putExtra("EMAIL", My_Email);
        startActivity(Set_Profile_Intent);
    }

    private void Sign_Out() {
        SharedPreferences My_Data = getActivity().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = My_Data.edit();
        editor.clear();
        editor.commit();
        Intent Go_Login = new Intent(getActivity(), Google_Login_Activity.class);
        Go_Login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mAuth.signOut();
        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        startActivity(Go_Login);
        getActivity().finish();
    }

    private void Set_My_Data(){
        SharedPreferences My_Data = getActivity().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
        My_Email = My_Data.getString("EMAIL", null);
        Picasso.get().load(My_Img).into(binding.SettingFragmentMyProfileImgCiv);
        binding.SettingFragmentMyNameTv.setText("이름: " + My_Name);
        binding.SettingFragmentMyUidTv.setText("#ID: " + My_Uid);
    }

    private void Get_My_Posts() {
        adapter.Get_Post_Single();
        adapter.Get_Post_Child_Listener();
        adapter.notifyDataSetChanged();
    }
}