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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bigjeon.grumbling.adapter.Color_Adapter_OnClickListener;
import com.bigjeon.grumbling.adapter.Color_Select_Rcv_Adapter;
import com.bigjeon.grumbling.adapter.DB_Posting_Bacground_GIF_Adapter;
import com.bigjeon.grumbling.adapter.Gif_OnClikListener;
import com.bigjeon.grumbling.data.DB_Posting_Background_GIF;
import com.bigjeon.grumbling.data.Post_Data;
import com.bumptech.glide.Glide;
import com.example.grumbling.R;
import com.example.grumbling.databinding.ActivityPostWriteBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private static final String TAG = "My_Post_Check";
    public static final String TAG_POST_WRITE = "Post_Dialog";
    private String STATE;
    private Post_Data Post;
    private ActivityPostWriteBinding binding;
    private DB_Posting_Bacground_GIF_Adapter adapter;
    private Color_Select_Rcv_Adapter color_adapter;
    private String Grade_All = "모든 사용자";
    private String Grade_Friends = "친구 공개";
    private String Grade_Secret = "비공개";
    private String Post_Title;
    private String User_Name;
    private String User_Uid;
    private String Posting_Content;
    private String Posting_Grade = Grade_All;
    private int Posting_Content_Size = 45;
    private int Posting_Content_Color = R.color.black;
    private int Posting_Content_BackColor = R.color.Transparent_Black40;
    private String Posting_Write_Date;
    private String Background_Img_String = null;
    private Uri Background_Img_Uri;
    private int Favorite_Count = 0;
    private int Declared_Count = 0;
    private String Set_Status = "Background";
    private String Background_Status = "String";
    private HashMap<String, Boolean> Favorite = new HashMap<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Posts");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_write);
        binding.setPostWriteActivityBinding(this);

        Intent get_key = getIntent();
        STATE = get_key.getStringExtra("KEY");
        Post_Title = get_key.getStringExtra("TITLE");

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
                Toast.makeText(Post_Write_Activity.this, "현재 글자 크기 : " + Posting_Content_Size, Toast.LENGTH_SHORT).show();
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
        //텍스트 색 지정
        Set_Content_Color();
        //텍스트 배경 색 지정
        Set_Content_Back_Color();
        binding.DialogPostingBackgroundGallery.setOnClickListener(v -> Get_Img_In_Gallery());
        binding.DialogPostingBackgroundDBRcv.setAdapter(adapter);
    }

    private void Get_Selected_Posts() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    String Title = data.getValue(Post_Data.class).getPost_Title();
                    if (Title.equals(Post_Title)){
                        Post = data.getValue(Post_Data.class);
                        Data_Adjust(Post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void Data_Adjust(Post_Data post) {
        binding.DialogPostingContent.setText(post.getContent());
        binding.DialogPostingContent.setTextSize(Dimension.DP, post.getContent_Text_Size());
        binding.DialogPostingContent.setBackgroundColor(ContextCompat.getColor(this, post.getContent_Back_Color()));
        binding.DialogPostingContent.setTextColor(ContextCompat.getColor(this, post.getContent_Text_Color()));
        Glide.with(this).load(post.getPost_Background()).into(binding.DialogPostingBackground);
        binding.DialogPostingSetGrade.setText(post.getGrade());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        Post_Title = post.getPost_Title();
        User_Uid = post.getUser_Uid();
        Posting_Content = post.getContent();
        Posting_Grade = post.getGrade();
        Posting_Content_Size = post.getContent_Text_Size();
        Posting_Content_Color = post.getContent_Text_Color();
        Posting_Content_BackColor = post.getContent_Back_Color();
        Posting_Write_Date = post.getPost_Write_Date();
        Background_Img_String = post.getPost_Background();
        Favorite_Count = post.getFavorite_Count();
        Declared_Count = post.getDeclared_Count();
        Favorite = post.getFavorite();
    }

    private void Set_Content_Back_Color() {
        color_adapter = new Color_Select_Rcv_Adapter(this);
        color_adapter.Set_Color_List(0);
        binding.DialogPostingContentTextBackColorRcv.setAdapter(color_adapter);
        color_adapter.setOnClickListener(new Color_Adapter_OnClickListener() {
            @Override
            public void onItemClick(Color_Select_Rcv_Adapter.Holder_Color holder_color, View v, int pos) {
                Posting_Content_BackColor = color_adapter.Get_Color(pos);
                binding.DialogPostingContent.setBackgroundColor(ContextCompat.getColor(Post_Write_Activity.this, Posting_Content_BackColor));
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(RecyclerView.HORIZONTAL);
        binding.DialogPostingContentTextBackColorRcv.setLayoutManager(lm);
        binding.DialogPostingContentTextBackColorRcv.setHasFixedSize(true);
        color_adapter.notifyDataSetChanged();
    }

    private void Set_Content_Color() {
        color_adapter = new Color_Select_Rcv_Adapter(this);
        color_adapter.Set_Color_List(0);
        binding.DialogPostingContentTextColorRcv.setAdapter(color_adapter);
        color_adapter.setOnClickListener(new Color_Adapter_OnClickListener() {
            @Override
            public void onItemClick(Color_Select_Rcv_Adapter.Holder_Color holder_color, View v, int pos) {
                Posting_Content_Color = color_adapter.Get_Color(pos);
                binding.DialogPostingContent.setTextColor(ContextCompat.getColor(Post_Write_Activity.this, Posting_Content_Color));
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(RecyclerView.HORIZONTAL);
        binding.DialogPostingContentTextColorRcv.setLayoutManager(lm);
        binding.DialogPostingContentTextColorRcv.setHasFixedSize(true);
        color_adapter.notifyDataSetChanged();
    }

    private void Get_Gif_In_raw() {
        adapter = new DB_Posting_Bacground_GIF_Adapter();
        adapter.Add_Gif(new DB_Posting_Background_GIF("https://firebasestorage.googleapis.com/v0/b/grumber-9d1b9.appspot.com/o/Background_Images_GIF%2FTwinkleLED.gif?alt=media&token=8f32a89f-63cd-4927-bc93-a542af5be96d", "1번 배경"));
        adapter.Add_Gif(new DB_Posting_Background_GIF("https://firebasestorage.googleapis.com/v0/b/grumber-9d1b9.appspot.com/o/Background_Images_GIF%2Fmulti_colred_motion.gif?alt=media&token=f08c3e46-fa42-4a9d-9773-a6b491106a49", "2번 배경"));

        binding.DialogPostingBackgroundDBRcv.setAdapter(adapter);
        adapter.setOnClickListener(new Gif_OnClikListener() {
            @Override
            public void onItemClcick(DB_Posting_Bacground_GIF_Adapter.Holder_Gif holder_gif, View view, int position) {
                Toast.makeText(Post_Write_Activity.this, "확인", Toast.LENGTH_SHORT).show();
                Background_Img_String = adapter.Get_Gif(position).getGIF();
                Background_Status = "String";
                Glide.with(Post_Write_Activity.this).load(Background_Img_String).into(binding.DialogPostingBackground);
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(RecyclerView.HORIZONTAL);
        binding.DialogPostingBackgroundDBRcv.setLayoutManager(lm);
        binding.DialogPostingBackgroundDBRcv.setHasFixedSize(true);

    }

    //================================================================================================

    //App_Main_Activity에서 넘겨준 유저 정보 가져옴
    private void Get_User_Profile() {
        SharedPreferences sharedPreferences = getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        User_Name = sharedPreferences.getString("NAME", null);
        User_Uid = sharedPreferences.getString("UID", null);
        binding.DialogPostingUserName.setText(User_Name);
        Picasso.get().load(sharedPreferences.getString("IMG", null)).into(binding.DialogPostingUserImg);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("공개 범위 설정").setItems(Grade, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Posting_Grade = Grade[which];
                binding.DialogPostingSetGrade.setText(Posting_Grade);
                Toast.makeText(Post_Write_Activity.this, "공개 범위 : " + Posting_Grade, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    //작성 완료된 포스트 파이어베이스에 저장
    private void Upload_Post() {
        Posting_Content = binding.DialogPostingContent.getText().toString();
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        Posting_Write_Date = simpledate.format(date);
        if (STATE.equals("CREATE")){
            Post_Title = Posting_Write_Date + User_Uid;
        }
        Post = new Post_Data(
                Post_Title,
                User_Uid,
                Posting_Content,
                Posting_Grade,
                Posting_Content_Size,
                Posting_Content_Color,
                Posting_Content_BackColor,
                Posting_Write_Date,
                Background_Img_String,
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
                                Post.setPost_Background(uri.toString());
                                reference.child(Post_Title).setValue(Post);
                            }
                        });
                    }
                });
            }else{
                reference.child(Post_Title).setValue(Post);
            }
            Toast.makeText(Post_Write_Activity.this, "게시글이 정상적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(Post_Write_Activity.this, "최소 3글자 이상 입력해 주세요!", Toast.LENGTH_SHORT).show();
        }
    }
}