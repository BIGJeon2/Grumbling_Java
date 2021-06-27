package com.bigjeon.grumbling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bigjeon.grumbling.data.User_Profile;
import com.example.grumbling.R;
import com.example.grumbling.databinding.Profile_Binding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Set_User_Profile_Activity extends AppCompatActivity {
    public static final String CODE_FIRST_SET = "FIRST_SET";
    Profile_Binding binding;
    private Uri Img_Uri;
    private String My_Uid;
    private String My_Name;
    private String My_Img;
    private String My_Email;

    private String Intent_Code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_user_profile);
        binding.setUserProfileSetActivity(this);

        //로그인 화면에서 가져온 UID값
        Intent intent = getIntent();
        Intent_Code = intent.getStringExtra("CODE");
        My_Uid = intent.getStringExtra("UID");
        My_Email = intent.getStringExtra("EMAIL");
        //Cirle_Image_Btn 클릭시 갤러리에서 사진 가져오기
        binding.UserImg.setOnClickListener(v -> Get_Img_From_Gallery());
        //FireSotre에 프로필 저장 / 업데이트
        binding.CompleteBtn.setOnClickListener(v -> Upload_User_Profile());

    }

    private void Get_Img_From_Gallery() {
        Intent Get_Img = new Intent(Intent.ACTION_PICK);
        Get_Img.setType("image/*");
        startActivityForResult(Get_Img, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 10:
            if(resultCode == RESULT_OK){
                Img_Uri = data.getData();
                Picasso.get().load(Img_Uri).into(binding.UserImg);
            } break;
        }
    }

    private void Upload_User_Profile() {
        My_Name = binding.UserName.getText().toString();
        if(My_Name.length() < 2){
            Toast.makeText(this, "사용하실 이름을 모두 작성해 주세요(2글자 이상)", Toast.LENGTH_SHORT).show();
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final ProgressDialog dialog = new ProgressDialog(Set_User_Profile_Activity.this);
                    dialog.setIndeterminate(true);
                    dialog.setMessage("잠시만 기다려 주세요");
                    dialog.show();
                }
            }, 300);
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
                                            editor.commit();
                                            Go_App_Main();
                                            finish();
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

    private void Go_App_Main(){
        Intent Go_App_Main = new Intent(this, App_Main_Activity.class);
        startActivity(Go_App_Main);
        finish();
    }
}