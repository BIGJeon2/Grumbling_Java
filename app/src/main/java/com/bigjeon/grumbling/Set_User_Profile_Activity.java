package com.bigjeon.grumbling;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bigjeon.grumbling.data.User_Profile;
import com.example.grumbling.R;
import com.example.grumbling.databinding.Profile_Binding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Set_User_Profile_Activity extends AppCompatActivity {
    public static final String CODE_FIRST_SET = "FIRST_SET";
    public static final String CODE_CHANGE_SET = "CHANGE_SET";
    private static final String TAG = "My_Post_Check";
    private ProgressBar progressBar;
    private Profile_Binding binding;
    private Uri Img_Uri = null;
    private String My_Uid;
    private String My_Name;
    private String My_Img;
    private String My_Location;
    private String My_State_Msg;
    private String Default_Img = "https://firebasestorage.googleapis.com/v0/b/grumber-9d1b9.appspot.com/o/Profile_Images%2Fuser_profile_default_img.png?alt=media&token=dfc9c293-5410-4a4b-bda9-34a1e8574066";
    private String My_Email;
    private Boolean Img_Pick_State = false;
    private String Intent_Code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_user_profile);
        binding.setUserProfileSetActivity(this);

        //로그인 화면에서 가져온 UID값
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        Intent_Code = intent.getStringExtra("CODE");
        My_Uid = intent.getStringExtra("UID");
        My_Email = intent.getStringExtra("EMAIL");
        My_Img = Default_Img;

        if (Intent_Code.equals("CHANGE_SET")){
            SharedPreferences data = getSharedPreferences("My_Data", MODE_PRIVATE);
            My_Img = data.getString("IMG", null);
            My_Name = data.getString("NAME", null);
            My_Email = data.getString("EMAIL", null);
            My_Uid = data.getString("UID", null);
            My_Location = data.getString("LOCATION", null);
            My_State_Msg = data.getString("STATE_MSG", null);
            Picasso.get().load(My_Img).into(binding.UserImg);
            binding.UserName.setText(My_Name);
            binding.UserLocation.setText(My_Location);
            binding.UserStateMSG.setText(My_State_Msg);
        }
        //Cirle_Image_Btn 클릭시 갤러리에서 사진 가져오기
        binding.SetUserImgFromGalleryBtn.setOnClickListener(v -> Get_Img_From_Gallery());
        binding.SetUserImgDefaultBtn.setOnClickListener(v -> Set_Default_Img());
        //FireSotre에 프로필 저장 / 업데이트
        binding.CompleteBtn.setOnClickListener(v -> Upload_User_Profile());

    }

    private void Set_Default_Img() {
        My_Img = Default_Img;
        Img_Pick_State = false;
        Picasso.get().load(R.drawable.user_profile_default_img).into(binding.UserImg);
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                        Img_Uri = result;
                        if (Img_Uri != null){
                            Picasso.get().load(Img_Uri).into(binding.UserImg);
                            Img_Pick_State = true;
                        }
                    }
            });

    private void Get_Img_From_Gallery() {
        Picasso.get().load(My_Img).into(binding.UserImg);
        mGetContent.launch("image/*");
    }

    private void Upload_User_Profile() {

        My_Name = binding.UserName.getText().toString();
        My_State_Msg = binding.UserStateMSG.getText().toString();
        My_Location = binding.UserLocation.getText().toString();

        if(My_Name.length() < 2 || My_Name.length() > 7){
            Toast.makeText(this, "사용하실 이름을 작성해 주세요(2글자 이상 6글자 이하)", Toast.LENGTH_SHORT).show();
        } else if (My_Location.length() <= 0){
            Toast.makeText(this, "거주 중인 지역을 작성해 주세요(시/도 까지만 입력해 주세요)", Toast.LENGTH_SHORT).show();
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar = new ProgressBar(Set_User_Profile_Activity.this, null, android.R.attr.progressBarStyleLarge);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    binding.SetUserProfileProgreesBar.addView(progressBar, params);
                    Toast.makeText(Set_User_Profile_Activity.this, "잠시만 기다려 주세요", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }, 300);
            if (Img_Pick_State == true){
                SimpleDateFormat date = new SimpleDateFormat("yyyMMddhhmmss");
                FirebaseStorage FireStorage = FirebaseStorage.getInstance();
                String File_Name = date.format(new Date()) + ".png";
                final StorageReference Img_Ref =FireStorage.getReference("Profile_Images/" + File_Name);
                //스토리지에 파일을 저장했으니 바로 경로 가져옴
                UploadTask uploadTask = Img_Ref.putFile(Img_Uri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Img_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                My_Img = uri.toString();
                                HashMap<String, Object> My_Profile = new HashMap<>();
                                My_Profile.put("EMAIL", My_Email);
                                My_Profile.put("UID", My_Uid);
                                My_Profile.put("Name", My_Name);
                                My_Profile.put("Img", My_Img);
                                My_Profile.put("Location", My_Location);
                                My_Profile.put("State_Msg", My_State_Msg);
                                FirebaseFirestore DB = FirebaseFirestore.getInstance();
                                DB.collection("Users")
                                        .document(My_Uid)
                                        .set(My_Profile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = My_Data.edit();
                                                editor.putString("EMAIL", My_Email);
                                                editor.putString("UID", My_Uid);
                                                editor.putString("NAME", My_Name);
                                                editor.putString("IMG", My_Img);
                                                editor.putString("LOCATION", My_Location);
                                                editor.putString("STATE_MSG", My_State_Msg);
                                                editor.commit();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Go_App_Main();
                                            }
                                        });
                            }
                        });
                    }
                });
            }else{
                HashMap<String, Object> My_Profile = new HashMap<>();
                My_Profile.put("EMAIL", My_Email);
                My_Profile.put("UID", My_Uid);
                My_Profile.put("Name", My_Name);
                My_Profile.put("Img", My_Img);
                My_Profile.put("Location", My_Location);
                My_Profile.put("State_Msg", My_State_Msg);
                FirebaseFirestore DB = FirebaseFirestore.getInstance();
                DB.collection("Users")
                        .document(My_Uid)
                        .set(My_Profile)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = My_Data.edit();
                                editor.putString("EMAIL", My_Email);
                                editor.putString("UID", My_Uid);
                                editor.putString("NAME", My_Name);
                                editor.putString("IMG", My_Img);
                                editor.putString("LOCATION", My_Location);
                                editor.putString("STATE_MSG", My_State_Msg);
                                editor.commit();
                                progressBar.setVisibility(View.INVISIBLE);
                                Go_App_Main();
                            }
                        });
            }
        }
    }

    private void Go_App_Main(){
        Intent Go_App_Main = new Intent(this, App_Main_Activity.class);
        startActivity(Go_App_Main);
        finish();
    }

}