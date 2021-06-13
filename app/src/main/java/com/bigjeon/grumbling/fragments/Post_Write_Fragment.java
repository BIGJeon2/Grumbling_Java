package com.bigjeon.grumbling.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.PostWriteFragmentBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

public class Post_Write_Fragment extends Fragment {
    //포트팅에 들어갈 데이터 초기값 설정(Default 값)
    private PostWriteFragmentBinding binding;

    private String User_Name;
    private String User_Uid;
    private String User_Img;
    private String Posting_Content;
    private String Posting_Grade = "All_Users"; //미구현
    private int Posting_Content_Size = 10;
    private int Posting_Content_Color = R.color.black; //미구현
    private int Posting_Content_BackColor = R.color.Transparent_Black; //미구현
    private String Posting_Write_Date;
    private int Post_Background; //미구현
    private int Favorite_Count = 0;
    private int Declared_Count = 0;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.post__write__fragment, container, false);
        View root = binding.getRoot();

        Get_User_Profile();

        binding.DialogPostingCompleteCIV.setOnClickListener(v -> Upload_Post());
        //글자 크기
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
                Toast.makeText(getActivity(), "현재 글자 크기 : " + Posting_Content_Size, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
    //App_Main_Activity에서 넘겨준 유저 정보 가져옴
    private void Get_User_Profile() {
        if (getArguments() != null){
            User_Uid = getArguments().getString("UID");
            User_Name = getArguments().getString("NAME");
            User_Img = getArguments().getString("IMG");

            binding.DialogPostingUserName.setText(User_Name);
            Picasso.get().load(User_Img).into(binding.DialogPostingUserImg);
        }
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
        Toast.makeText(getActivity(), Posting_Write_Date, Toast.LENGTH_SHORT).show();
    }
}