package com.bigjeon.grumbling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.grumbling.R;

public class MainActivity extends AppCompatActivity {

    private ImageView Loading_View;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Change_Status();
        }
    }
        private void Change_Status(){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Loading_View = findViewById(R.id.Loading_GIF);
        Glide.with(this).asGif().load(R.drawable.loading).into(Loading_View);

        SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
        //3초간 대기후 작동
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (My_Data.getString("UID", null) == null){
                    //백그라운드 작업으로 파이어 베이스에서 회원 정보 가져오기
                    //첫 사용자일시 구글 로그인 화면으로 이동
                    Intent Go_Google_Login = new Intent(MainActivity.this, Google_Login_Activity.class);
                    startActivity(Go_Google_Login);
                    finish();
                }else{
                    //기존 유저일시 바로 App_Main으로 이동
                    Toast.makeText(MainActivity.this, My_Data.getString("NAME", null) + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                    Intent Go_App_Main = new Intent(MainActivity.this, App_Main_Activity.class);
                    startActivity(Go_App_Main);
                    finish();
                }
            }
        }, 1000);
    }
}