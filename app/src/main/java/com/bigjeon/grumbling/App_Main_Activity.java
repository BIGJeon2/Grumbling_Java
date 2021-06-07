package com.bigjeon.grumbling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.grumbling.App_Main_Binding;
import com.example.grumbling.R;
import com.squareup.picasso.Picasso;

public class App_Main_Activity extends AppCompatActivity {

    App_Main_Binding binding;
    private String My_Uid;
    private String My_Img;
    private String My_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_main);
        binding.setAppMainActivity(this);

        Set_My_Data();

    }

    private void Set_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
        Picasso.get().load(My_Img).into(binding.AppMainUserImgCircleImv);
        binding.AppMainUserNameTv.setText(My_Name);
    }
}