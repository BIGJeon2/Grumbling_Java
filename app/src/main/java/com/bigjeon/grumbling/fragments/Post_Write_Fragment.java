package com.bigjeon.grumbling.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;

import com.example.grumbling.databinding.PostWriteFragmentBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Objects;

public class Post_Write_Fragment extends DialogFragment {
    public static final String TAG_POST_WRITE = "Post_Dialog";
    //포트팅에 들어갈 데이터 초기값 설정(Default 값)
    private PostWriteFragmentBinding binding;
    private String Grade_All = "모든 사용자";
    private String Grade_Friends = "친구 공개";
    private String Grade_Secret = "비공개";
    private Context context;
    private String User_Name;
    private String User_Uid;
    private String User_Img;
    private String Posting_Content;
    private String Posting_Grade = Grade_All; //미구현
    private int Posting_Content_Size = 10;
    private int Posting_Content_Color = R.color.black; //미구현
    private int Posting_Content_BackColor = R.color.Transparent_Black; //미구현
    private String Posting_Write_Date;
    private int Post_Background; //미구현
    private int Favorite_Count = 0;
    private int Declared_Count = 0;
    private String Set_Status = "Background";

    public Post_Write_Fragment(){}
    public static Post_Write_Fragment getInstance(){
        Post_Write_Fragment post_write_fragment = new Post_Write_Fragment();
        return post_write_fragment;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.post__write__fragment, container, false);
        View root = binding.getRoot();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Get_User_Profile();
        //글자 크기==============================================
        binding.DialogPostingContentTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.DialogPostingContent.setTextSize(Dimension.DP, seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binding.DialogPostingContent.setTextSize(Dimension.DP, seekBar.getProgress());
                Posting_Content_Size = seekBar.getProgress();
                Toast.makeText(getContext(), "현재 글자 크기 : " + Posting_Content_Size, Toast.LENGTH_SHORT).show();
            }
        });
        //보안 등급 설정 버튼===========================================
        binding.DialogPostingSetGrade.setOnClickListener(v -> Set_Posting_Grade());
        //백그라운드 이미지 버튼==========================================
        binding.DialogPostingSetImg.setOnClickListener(v -> Change_Set_Status("Background"));
        //텍스트 설정 창 버튼============================================
        binding.DialogPostingSetText.setOnClickListener(v -> Change_Set_Status("Text"));
        //포스팅 완료 버튼==============================================
        binding.DialogPostingCompleteCIV.setOnClickListener(v -> Upload_Post());


        //백그라운드 이미지 설정 부분
        binding.DialogPostingBackgroundGallery.setOnClickListener(v -> Get_Img_In_Gallery());
        return root;
    }

    //================================================================================================

    //App_Main_Activity에서 넘겨준 유저 정보 가져옴
    private void Get_User_Profile() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        User_Uid = sharedPreferences.getString("UID", null);
        User_Name = sharedPreferences.getString("NAME", null);
        User_Img = sharedPreferences.getString("IMG", null);
        binding.DialogPostingUserName.setText(User_Name);
        Picasso.get().load(User_Img).into(binding.DialogPostingUserImg);
    }

    //설정 창 상태 변경
    private void Change_Set_Status(String Status){
        if (Status.equals("Background")){
            if(!Set_Status.equals(Status)){
                Set_Status = Status;
                binding.DialogPostingTextSetContainer.setVisibility(View.INVISIBLE);
                binding.DialogPostingBackgroundContainer.setVisibility(View.VISIBLE);
            }
        }else if(Status.equals("Text")){
            if (!Set_Status.equals(Status)){
                Set_Status = Status;
                binding.DialogPostingBackgroundContainer.setVisibility(View.INVISIBLE);
                binding.DialogPostingTextSetContainer.setVisibility(View.VISIBLE);
            }
        }
    }
    //갤러리 사진 가져오기
    private void Get_Img_In_Gallery() {
        Intent Get_Img = new Intent(Intent.ACTION_PICK);
        Get_Img.setType("image/*");
        startActivityForResult(Get_Img, 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //보안 등급 설정
    private void Set_Posting_Grade() {
        String[] Grade = {Grade_All, Grade_Friends, Grade_Secret};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("공개 범위 설정").setItems(Grade, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Posting_Grade = Grade[which];
                binding.DialogPostingSetGrade.setText(Posting_Grade);
                Toast.makeText(getContext(), "공개 범위 : " + Posting_Grade, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    //작성 완료된 포스트 파이어베이스에 저장
    private void Upload_Post() {
        Posting_Content = binding.DialogPostingContent.getText().toString();
        Posting_Write_Date = SimpleDateFormat.getDateInstance().format("yyyy-MM-dd-hh:mm:ss");
        //작성글이 있을 경우에만 저장
        if (Posting_Content.length() != 2){
            //글 업로드
            Post_Data post = new Post_Data(
                    User_Name,
                    User_Img,
                    User_Uid,
                    Posting_Content,
                    Posting_Grade,
                    Posting_Content_Size,
                    Posting_Content_Color,
                    Posting_Content_BackColor,
                    Posting_Write_Date,
                    Post_Background,
                    Favorite_Count,
                    Declared_Count
            );
        }
        Toast.makeText(getContext(), Posting_Write_Date, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}