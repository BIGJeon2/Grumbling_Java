package com.bigjeon.grumbling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.bigjeon.grumbling.fragments.Post_Write_Fragment;
import com.example.grumbling.App_Main_Binding;
import com.example.grumbling.R;
import com.squareup.picasso.Picasso;

public class App_Main_Activity extends AppCompatActivity {

    App_Main_Binding binding;
    public String My_Uid;
    public String My_Img;
    public String My_Name;
    private Post_View_Fragment post_view_fragment = new Post_View_Fragment();
    private Post_Write_Fragment post_write_fragment = new Post_Write_Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_main);
        binding.setAppMainActivity(this);

        Set_My_Data();
        Change_Fragment("Post_View");
        binding.AppMainWritePostStartCircleImgBtn.setOnClickListener(v -> Change_Fragment("Post_Write"));
    }

    private void Set_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
        Picasso.get().load(My_Img).into(binding.AppMainUserImgCircleImv);
        binding.AppMainUserNameTv.setText(My_Name);
    }
    //프레그먼트 전환
    private void Change_Fragment(String FRAGMENT_ID){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        switch (FRAGMENT_ID){
            case "Post_View" :
                fragmentTransaction.replace(R.id.App_Main_Fragment, post_view_fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case "Post_Write" :
                Bundle bundle = new Bundle();
                bundle.putString("UID", My_Uid);
                bundle.putString("NAME", My_Name);
                bundle.putString("IMG", My_Img);
                post_write_fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.App_Main_Fragment, post_write_fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                binding.AppMainWritePostStartCircleImgBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }
}