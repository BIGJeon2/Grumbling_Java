package com.bigjeon.grumbling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Request_Friends_Data;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.ActivityUserProfileViewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class User_Profile_View_activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String User_Uid;
    private DatabaseReference reference;
    private String Get_Post_Key = "나의 게시글";
    private String Request_Friend_State = "NONE";
    private ActivityUserProfileViewBinding binding;
    private Post_View_Rcv_Adapter adapter;
    private ArrayList<Post_Data> list = new ArrayList<>();
    private String My_Uid;
    private String My_Name;
    private String My_Img;
    private String My_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile_view);
        binding.setShowUserProfileBinding(this);

        Get_My_Data();

        Intent intent = getIntent();
        User_Uid = intent.getStringExtra("UID");
        if (!User_Uid.equals(My_Uid)) {
            Set_Users_Data();
            Get_Post_Key = User_Uid;
        } else {
            Picasso.get().load(My_Img).into(binding.SettingFragmentMyProfileImgCiv);
            binding.SettingFragmentMyNameTv.setText("#." + My_Name);
        }

        mAuth = FirebaseAuth.getInstance();

        RecyclerView rcv = binding.SettingFragmnetUserPostsRCV;
        adapter = new Post_View_Rcv_Adapter(this, list, Get_Post_Key);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rcv.setLayoutManager(lm);
        rcv.setAdapter(adapter);
        rcv.setHasFixedSize(true);

        Check_Friend_State();
        Get_Users_Posts();

        binding.SettingFragmentSendFriendRequestBtn.setOnClickListener(v -> Send_Friend_Request());
    }

    private void Set_Users_Data() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("UID", User_Uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        binding.SettingFragmentMyNameTv.setText(document.get("Name").toString());
                        Picasso.get().load(document.get("Img").toString()).into(binding.SettingFragmentMyProfileImgCiv);
                        break;
                    }
                }
            }
        });
    }
    private void Get_Users_Posts() {
        adapter.Get_Post_Single();
        adapter.Get_Post_Child_Listener();
        adapter.notifyDataSetChanged();
    }

    private void Get_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
        My_Email = My_Data.getString("EMAIL", null);
    }

    private void Send_Friend_Request(){

        SimpleDateFormat simpledate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String Send_Date = simpledate.format(date);
        //내 DB에 저장
        Request_Friends_Data request_My = new Request_Friends_Data("ING", User_Uid, Send_Date);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Send_Friends_Request").child(User_Uid);
        reference.setValue(request_My);
        //상대 DB에 저장
        Request_Friends_Data request = new Request_Friends_Data("ING", My_Uid, Send_Date);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("Receive_Friends_Request").child(My_Uid);
        reference.setValue(request);
        binding.SettingFragmentSendFriendRequestBtn.setText("응답 대기중...");
    }

    private void Check_Friend_State(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("Receive_Friends_Request").child(My_Uid);
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Request_Friend_State = "NONE";
                }else{
                    Request_Friends_Data request = task.getResult().getValue(Request_Friends_Data.class);
                    if (request == null){
                        Request_Friend_State = "NONE";
                    }else if (request.getState().equals("ING")){
                        Request_Friend_State = "ING";
                        binding.SettingFragmentSendFriendRequestBtn.setText("응답 대기중...");
                    }else if(request.getState().equals("ACCEPT")){
                        Request_Friend_State = "ACCEPT";
                        binding.SettingFragmentSendFriendRequestBtn.setVisibility(View.GONE);
                    }else{
                        Request_Friend_State = "REJECT";
                        binding.SettingFragmentSendFriendRequestBtn.setText("거절됨");
                    }
                }
            }
        });
    }
}