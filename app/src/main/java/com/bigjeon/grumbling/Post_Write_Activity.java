package com.bigjeon.grumbling;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bigjeon.grumbling.adapter.Color_Adapter_OnClickListener;
import com.bigjeon.grumbling.adapter.Color_Select_Rcv_Adapter;
import com.bigjeon.grumbling.adapter.DB_Posting_Bacground_GIF_Adapter;
import com.bigjeon.grumbling.adapter.Fragment_Swipe_Adapter;
import com.bigjeon.grumbling.adapter.Gif_OnClikListener;
import com.bigjeon.grumbling.adapter.Post_Write_ViewPager2_Adapter;
import com.bigjeon.grumbling.data.DB_Posting_Background_GIF;
import com.bigjeon.grumbling.data.Post_Data;
import com.bigjeon.grumbling.dialogs.Post_Write_Loading_ProgressDialog;
import com.bigjeon.grumbling.factory.Post_Write_VM_Factory;
import com.bigjeon.grumbling.viewmodel.Post_Write_ViewModel;
import com.bumptech.glide.Glide;
import com.example.grumbling.R;
import com.example.grumbling.databinding.ActivityPostWriteBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Post_Write_Activity extends AppCompatActivity {
    private ActivityPostWriteBinding binding;
    private Post_Write_ViewPager2_Adapter ViewPager_Adapter;
    private Post_Write_Loading_ProgressDialog progressDialog;
    private String STATE = "CREATE";
    private int key = 0;
    private Post_Data post_data;
    private String Grade_All = "모든 사용자";
    private String Grade_Friends = "친구 공개";
    private String Grade_Secret = "비공개";
    private String User_Name;
    private String User_Uid;
    private String Post_Title;
    private String Posting_Grade = Grade_All;
    private String Posting_Write_Date;
    private HashMap<String, Boolean> Favorite = new HashMap<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Posts");
    private Post_Write_ViewModel VM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_write);
        binding.setPostWriteActivityBinding(this);

        progressDialog = new Post_Write_Loading_ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //뷰모델 호출 / 데이터 변경 이벤트시 데이터 바로 적용
        VM = new ViewModelProvider(this, new Post_Write_VM_Factory()).get(Post_Write_ViewModel.class);

        Intent get_key = getIntent();
        STATE = get_key.getStringExtra("KEY");
        Post_Title = get_key.getStringExtra("TITLE");
        if (STATE.equals("CHANGE")){
            Get_Selected_Posts();
        }else{
            Toast.makeText(this, STATE, Toast.LENGTH_SHORT).show();
            Set_First_State();
        }
        VM.get_Post().observe(this, post -> Data_Adjust(post_data));
        VM.getIMG_String().observe(this, img -> Glide.with(this).load(img).into(binding.DialogPostingBackground));
        VM.getIMG_URI().observe(this, img -> Glide.with(this).load(img).into(binding.DialogPostingBackground));

        //ViewPager2
        ViewPager_Adapter = new Post_Write_ViewPager2_Adapter(this);
        binding.PostWriteViewPager2.setAdapter(ViewPager_Adapter);
        binding.PostWriteViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.PostWriteViewPager2.setCurrentItem(0, true);
        binding.PostWriteViewPager2.setOffscreenPageLimit(1);
        binding.PostWriteViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0){
                    binding.PostWriteViewPager2.setCurrentItem(position);
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Button_Background_Change(position);
            }
        });
        //텍스트 설정 창 버튼============================================
        binding.DialogPostingSetText.setOnClickListener(v -> Change_Fragment_OnCLick(0));
        //백그라운드 이미지 버튼==========================================
        binding.DialogPostingSetImg.setOnClickListener(v -> Change_Fragment_OnCLick(1));

        //보안 등급 설정 버튼===========================================
        binding.DialogPostingSetGrade.setOnClickListener(v -> Set_Posting_Grade());
        //포스팅 완료 버튼==============================================
        binding.DialogPostingCompleteCIV.setOnClickListener(v -> Upload_Post());

    }
    //ViewPager2
    private void Change_Fragment_OnCLick(int i) {
        if (i == 0){
            binding.PostWriteViewPager2.setCurrentItem(0, true);
        }else{
            binding.PostWriteViewPager2.setCurrentItem(1, true);
        }
    }
    private void Button_Background_Change(int position){
        switch (position){
            case 0 :
                binding.DialogPostingSetText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFBB86FC")));
                binding.DialogPostingSetImg.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case 1 :
                binding.DialogPostingSetText.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                binding.DialogPostingSetImg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFBB86FC")));
                break;
        }
    }
    //수정작업일시 데이터 불러오기
    private void Get_Selected_Posts() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    String Title = data.getValue(Post_Data.class).getPost_Title();
                    if (Title.equals(Post_Title)){
                        post_data = data.getValue(Post_Data.class);
                        VM.set_Post(post_data);
                        binding.DialogPostingContent.setText(post_data.getContent());
                        if (post_data.getPost_Background() != null){
                            VM.setIMG_State("String");
                            VM.setIMG_String(post_data.getPost_Background());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //App_Main_Activity에서 넘겨준 유저 정보 가져옴
    private void Set_First_State() {
        SharedPreferences sharedPreferences = getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        User_Name = sharedPreferences.getString("NAME", null);
        User_Uid = sharedPreferences.getString("UID", null);
        binding.DialogPostingUserName.setText(User_Name);
        Picasso.get().load(sharedPreferences.getString("IMG", null)).into(binding.DialogPostingUserImg);
        post_data = new Post_Data(null,
                User_Uid,
                null,
                "모든 사용자",
                45,
                R.color.black,
                R.color.Transparent_Black30,
                null,
                null,
                0,
                0,
                Favorite);
        VM.set_Post(post_data);
        VM.setIMG_State("NONE");
    }
    //수정일시 데이터 적용시켜줌
    private void Data_Adjust(Post_Data post) {
        binding.DialogPostingContent.setTextSize(Dimension.DP, post.getContent_Text_Size());
        binding.DialogPostingContent.setBackgroundColor(ContextCompat.getColor(this, post.getContent_Back_Color()));
        binding.DialogPostingContent.setTextColor(ContextCompat.getColor(this, post.getContent_Text_Color()));
        binding.DialogPostingSetGrade.setText(post.getGrade());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (post.getPost_Background() != null && key == 0){
            Glide.with(this).load(post.getPost_Background()).into(binding.DialogPostingBackground);
            key++;
        }else if (post.getPost_Background() != null){
            if (VM.IMG_State.getValue().equals("String")){
                Glide.with(this).load(VM.getIMG_String().getValue()).into(binding.DialogPostingBackground);
            }else{
                Glide.with(this).load(VM.getIMG_URI().getValue()).into(binding.DialogPostingBackground);
            }
        }
        db.collection("Users").whereEqualTo("UID", post.getUser_Uid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        binding.DialogPostingUserName.setText(document.get("Name").toString());
                        Picasso.get().load(document.getString("Img")).into(binding.DialogPostingUserImg);
                        break;
                    }
                }
            }
        });
    }
    //보안 등급 설정
    private void Set_Posting_Grade() {
        String[] Grade = {Grade_All, Grade_Friends, Grade_Secret};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("공개 범위 설정").setItems(Grade, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                post_data.setGrade(Grade[which]);
                VM.set_Post(post_data);
                Toast.makeText(Post_Write_Activity.this, "공개 범위 : " + Posting_Grade, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    //작성 완료된 포스트 파이어베이스에 저장
    private void Upload_Post() {
        VM.Post.getValue().setContent(binding.DialogPostingContent.getText().toString());
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        Posting_Write_Date = simpledate.format(date);
        if (STATE.equals("CREATE")){
            VM.Post.getValue().setPost_Write_Date(Posting_Write_Date);
            VM.Post.getValue().setPost_Title(Posting_Write_Date + User_Uid);
        }
        //작성글이 있을 경우에만 저장
        if (VM.get_Post().getValue().getContent().length() > 2) {
            progressDialog.show();
            progressDialog.setCancelable(false);
            if (VM.getIMG_State().getValue().equals("Uri")){
                String File_Name = Posting_Write_Date + User_Uid + ".png";
                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference Post_Img_Ref = storage.getReference("Posting_Images/" + File_Name);
                //개인 사진 등록했으니 바로 경로 가져와줌
                UploadTask uploadTask = Post_Img_Ref.putFile(VM.getIMG_URI().getValue());
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Post_Img_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                VM.get_Post().getValue().setPost_Background(uri.toString());
                                reference.child(Post_Title).setValue(VM.get_Post().getValue());
                                progressDialog.dismiss();
                                Toast.makeText(Post_Write_Activity.this, "게시글이 정상적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }else {
                VM.get_Post().getValue().setPost_Background(VM.getIMG_String().getValue());
                reference.child(VM.get_Post().getValue().getPost_Title()).setValue(VM.get_Post().getValue());
                progressDialog.dismiss();
                Toast.makeText(Post_Write_Activity.this, "게시글이 정상적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
                finish();
            }
//            Toast.makeText(Post_Write_Activity.this, "게시글이 정상적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
//            finish();
        }else {
            Toast.makeText(Post_Write_Activity.this, "최소 3글자 이상 입력해 주세요!", Toast.LENGTH_SHORT).show();
        }
    }
}