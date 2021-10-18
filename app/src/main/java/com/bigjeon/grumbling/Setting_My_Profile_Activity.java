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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
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
    private String Get_Post_Key = "유저 게시글";
    private ActivitySettingMyProfileBinding binding;
    private Post_View_Rcv_Adapter adapter;
    private ArrayList<Post_Data> list = new ArrayList<>();
    private String My_Uid;
    private String My_Name;
    private String My_Img;
    private String My_Email;
    private String My_Location;
    private String My_State_Msg;
    private int Post_Count = 0;

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
            Get_Post_Key = User_Uid;
        }else{
            Picasso.get().load(My_Img).fit().into(binding.SettingFragmentMyProfileImgCiv);
            binding.SettingFragmentMyNameTv.setText(My_Name);
            binding.MyProfileActivityUserLocationTV.setText("#" + My_Location);
            binding.SettingActivityStateMsgTv.setText(" ' " + My_State_Msg + " ' ");
        }

        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference("Posts");

        RecyclerView rcv = binding.SettingFragmnetMyPostsRCV;
        adapter = new Post_View_Rcv_Adapter(this, list, Get_Post_Key, My_Uid, My_Name, My_Img, User_Uid);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rcv.setLayoutManager(lm);
        rcv.setAdapter(adapter);
        rcv.setHasFixedSize(true);

        Get_Users_Posts();
        binding.MyProfileBackIMV.setOnClickListener(v -> onBackPressed());
        binding.SettingFragmentSignOutBtn.setOnClickListener(v -> Sign_Out());
        binding.SettingFragmentChangeProfileBtn.setOnClickListener(v -> Go_Profile_Set_Act());
        binding.SettingFragmentUnregisterBtn.setOnClickListener( v -> Toast.makeText(this, "회원 탈퇴 기능은 베타버전에선 지원되지 않습니다. 추후 정식버전 출시시 이전 데이터들은 모두 일괄 삭제되오니, 참고 바랍니다!", Toast.LENGTH_SHORT).show());
    }

    private void Set_Users_Data() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("UID", User_Uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        binding.SettingFragmentMyNameTv.setText(document.get("Name").toString());
                        Picasso.get().load(document.get("Img").toString()).fit().into(binding.SettingFragmentMyProfileImgCiv);
                        binding.MyProfileActivityUserLocationTV.setText("#" + My_Location);
                        binding.SettingActivityStateMsgTv.setText(" ' " + My_State_Msg + " ' ");
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
        My_Location = My_Data.getString("LOCATION", null);
        My_State_Msg = My_Data.getString("STATE_MSG", null);
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
        GoogleSignInOptions option = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build();
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        GoogleSignInClient client = GoogleSignIn.getClient(this, option);
        client.signOut();
        client.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                restart(getApplicationContext());
            }
        });
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
                            Post_Count++;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    binding.UserProfilePostCount.setText(Integer.toString(adapter.getItemCount()));
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Get_Users_Posts();
    }

    private void restart(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }


}