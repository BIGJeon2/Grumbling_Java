package com.bigjeon.grumbling;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.bigjeon.grumbling.adapter.Post_View_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.ActivitySettingMyProfileBinding;
import com.example.grumbling.databinding.ActivityUserProfileViewBinding;
import com.example.grumbling.databinding.Profile_Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Setting_My_Profile_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String User_Uid;
    private DatabaseReference DB;
    private String Get_Post_Key = "나의 게시글";
    private ActivitySettingMyProfileBinding binding;
    private Post_View_Rcv_Adapter adapter;
    private ArrayList<Post_Data> list = new ArrayList<>();
    private String My_Uid;
    private String My_Name;
    private String My_Img;
    private String My_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_my_profile);
        binding.setMyProfileSettingBinding(this);

        Get_My_Data();

        Intent intent = getIntent();
        User_Uid = intent.getStringExtra("UID");
        if (!User_Uid.equals(My_Uid)){
            Set_Users_Data();
            binding.SettingFragmentSettingBtnsContainer.setVisibility(View.GONE);
            Get_Post_Key = User_Uid;
        }else{
            Picasso.get().load(My_Img).into(binding.SettingFragmentMyProfileImgCiv);
            binding.SettingFragmentMyNameTv.setText(My_Name);
            binding.MyProfileActivityUserUIDTV.setText("#" + My_Uid);
        }

        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference("Posts");

        RecyclerView rcv = binding.SettingFragmnetMyPostsRCV;
        adapter = new Post_View_Rcv_Adapter(this, list, Get_Post_Key);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rcv.setLayoutManager(lm);
        rcv.setAdapter(adapter);
        rcv.setHasFixedSize(true);

        Get_Users_Posts();

        binding.SettingFragmentSignOutBtn.setOnClickListener(v -> Sign_Out());
        binding.SettingFragmentChangeProfileBtn.setOnClickListener(v -> Go_Profile_Set_Act());
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
    private void Get_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
        My_Email = My_Data.getString("EMAIL", null);
    }

    private void Go_Profile_Set_Act() {
        Intent Set_Profile_Intent = new Intent(this, Set_User_Profile_Activity.class);
        Set_Profile_Intent.putExtra("UID", User_Uid);
        Set_Profile_Intent.putExtra("CODE", "CHANGE_SET");
        Set_Profile_Intent.putExtra("EMAIL", My_Email);
        startActivity(Set_Profile_Intent);
    }

    private void Sign_Out() {
        SharedPreferences My_Data = getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = My_Data.edit();
        editor.clear();
        editor.commit();
        Intent Go_Login = new Intent(this, Google_Login_Activity.class);
        Go_Login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mAuth.signOut();
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        startActivity(Go_Login);
        finish();
    }

    private void Get_Users_Posts() {
        Get_My_Post();
        adapter.Get_Post_Child_Listener();
        adapter.notifyDataSetChanged();
    }

    private void Get_My_Post(){
            DB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Post_Data post = data.getValue(Post_Data.class);
                        if (post.getUser_Uid().equals(mAuth.getCurrentUser().getUid())){
                            list.add(0, post);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
    }
}