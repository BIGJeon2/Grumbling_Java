package com.bigjeon.grumbling.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bigjeon.grumbling.adapter.DB_Posting_Bacground_GIF_Adapter;
import com.bigjeon.grumbling.adapter.Gif_OnClikListener;
import com.bigjeon.grumbling.data.DB_Posting_Background_GIF;
import com.bigjeon.grumbling.data.Post_Data;
import com.bumptech.glide.Glide;
import com.example.grumbling.R;

import com.example.grumbling.databinding.PostWriteFragmentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Post_Write_Fragment extends DialogFragment {
    private static final String TAG = "My_Post_Check";
    public static final String TAG_POST_WRITE = "Post_Dialog";
    //포트팅에 들어갈 데이터 초기값 설정(Default 값)
    private PostWriteFragmentBinding binding;
    private DB_Posting_Bacground_GIF_Adapter adapter;
    private String Grade_All = "모든 사용자";
    private String Grade_Friends = "친구 공개";
    private String Grade_Secret = "비공개";
    private String Post_Title;
    private Context context;
    private String User_Name;
    private String User_Uid;
    private String User_Img;
    private String Posting_Content;
    private String Posting_Grade = Grade_All;
    private int Posting_Content_Size = 10;
    private int Posting_Content_Color = R.color.black; //미구현
    private int Posting_Content_BackColor = R.color.Transparent_Black; //미구현
    private String Posting_Write_Date;
    private String Background_Img_String = null; //미구현
    private Uri Background_Img_Uri;
    private int Favorite_Count = 0;
    private int Declared_Count = 0;
    private String Set_Status = "Background";
    private String Background_Status = "String";
    private HashMap<String, Boolean> Favorite = new HashMap<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Posts");

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
        Get_Gif_In_raw();
        binding.DialogPostingBackgroundGallery.setOnClickListener(v -> Get_Img_In_Gallery());
        binding.DialogPostingBackgroundDBRcv.setAdapter(adapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);
    }

    private void Get_Gif_In_raw() {
        adapter = new DB_Posting_Bacground_GIF_Adapter();
        adapter.Add_Gif(new DB_Posting_Background_GIF("https://firebasestorage.googleapis.com/v0/b/grumber-9d1b9.appspot.com/o/Background_Images_GIF%2FTwinkleLED.gif?alt=media&token=8f32a89f-63cd-4927-bc93-a542af5be96d", "1번 배경"));
        adapter.Add_Gif(new DB_Posting_Background_GIF("https://firebasestorage.googleapis.com/v0/b/grumber-9d1b9.appspot.com/o/Background_Images_GIF%2Fmulti_colred_motion.gif?alt=media&token=f08c3e46-fa42-4a9d-9773-a6b491106a49", "2번 배경"));

        binding.DialogPostingBackgroundDBRcv.setAdapter(adapter);
        adapter.setOnClickListener(new Gif_OnClikListener() {
            @Override
            public void onItemClcick(DB_Posting_Bacground_GIF_Adapter.Holder_Gif holder_gif, View view, int position) {
                Toast.makeText(getContext(), "확인", Toast.LENGTH_SHORT).show();
                Background_Img_String = adapter.Get_Gif(position).getGIF();
                Background_Status = "String";
                Glide.with(getContext()).load(Background_Img_String).into(binding.DialogPostingBackground);
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(RecyclerView.HORIZONTAL);
        binding.DialogPostingBackgroundDBRcv.setLayoutManager(lm);
        binding.DialogPostingBackgroundDBRcv.setHasFixedSize(true);

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

    //갤러리 사진 가져오기
    private void Get_Img_In_Gallery() {
        Intent Get_Img = new Intent(Intent.ACTION_PICK);
        Get_Img.setType("image/*");
        mGetContent.launch("image/*");
    }
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    Background_Img_Uri = result;
                    Background_Status = "Uri";
                    Picasso.get().load(Background_Img_Uri).into(binding.DialogPostingBackground);
                }
            });

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
        Posting_Write_Date = new SimpleDateFormat("yyyMMddhhmmss").format(new Date());
        Post_Title = Posting_Write_Date + User_Uid;
        Post_Data post = new Post_Data(
                Post_Title,
                User_Name,//
                User_Img,//
                User_Uid,//
                Posting_Content,//
                Posting_Grade,//
                Posting_Content_Size,//
                Posting_Content_Color,
                Posting_Content_BackColor,
                Posting_Write_Date,//
                Background_Img_String,//
                Favorite_Count,
                Declared_Count,
                Favorite
        );
        //작성글이 있을 경우에만 저장
        if (Posting_Content.length() > 2) {
            if (Background_Status.equals("Uri")){
                String File_Name = Posting_Write_Date + User_Uid + ".png";
                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference Post_Img_Ref = storage.getReference("Posting_Images/" + File_Name);
                //개인 사진 등록했으니 바로 경로 가져와줌
                UploadTask uploadTask = Post_Img_Ref.putFile(Background_Img_Uri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Post_Img_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "Uri = "+ uri.toString());
                                post.setPost_Background(uri.toString());
                                reference.child(Post_Title).setValue(post);
                            }
                        });
                    }
                });
            }else{
                reference.child(Post_Title).setValue(post);
            }
            Toast.makeText(getContext(), "게시글이 정상적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
            dismiss();
        }else {
            Toast.makeText(getContext(), "최소 3글자 이상 입력해 주세요!", Toast.LENGTH_SHORT).show();
        }
    }
}