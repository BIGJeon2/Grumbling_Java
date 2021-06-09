package com.bigjeon.grumbling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.example.grumbling.App_Main_Binding;
import com.example.grumbling.R;
import com.squareup.picasso.Picasso;

public class App_Main_Activity extends AppCompatActivity {

    App_Main_Binding binding;
    public String My_Uid;
    public String My_Img;
    public String My_Name;
    private Post_View_Fragment post_view_fragment = new Post_View_Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_main);
        binding.setAppMainActivity(this);

        Set_My_Data();
        Change_Fragment("Post_View");
        binding.AppMainWritePostStartFloationgBtn.setOnClickListener(v -> Alert_Post_Dialog());
    }

    private void Alert_Post_Dialog() {
        //포스팅 작성 dialog 띄워줌
        Toast.makeText(getApplicationContext(), "글작성 띄워주기", Toast.LENGTH_SHORT).show();
    }

    private void Set_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
        Picasso.get().load(My_Img).into(binding.AppMainUserImgCircleImv);
        binding.AppMainUserNameTv.setText(My_Name);
    }

    private void Change_Fragment(String FRAGMENT_ID){
        switch (FRAGMENT_ID){
            case "Post_View" :
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.App_Main_Fragment, post_view_fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        }
    }
}