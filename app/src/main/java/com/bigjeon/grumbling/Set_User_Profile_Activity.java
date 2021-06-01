package com.bigjeon.grumbling;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigjeon.grumbling.data.User_Profile;
import com.example.grumbling.R;
import com.example.grumbling.databinding.Profile_Binding;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Set_User_Profile_Activity extends AppCompatActivity {

    Profile_Binding binding;
    private Uri Img_Uri;
    private String My_Uid;
    private String My_Name;
    private String My_Img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_user_profile);
        binding.setUserProfileSetActivity(this);

        binding.UserImg.setOnClickListener(v -> Get_Img_From_Gallery());
        binding.UserImg.setOnClickListener(v -> Upload_User_Profile());
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
            User_Profile My_Profile = new User_Profile(My_Uid, My_Name, My_Img, null, null, null);
        }
    }
}