package com.bigjeon.grumbling;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigjeon.grumbling.Model.Api;
import com.bigjeon.grumbling.Model.ApiCLient;
import com.bigjeon.grumbling.Model.Data;
import com.bigjeon.grumbling.Model.Model;
import com.bigjeon.grumbling.Model.NotificationModel;
import com.bigjeon.grumbling.adapter.Chat_rcv_Adapter;
import com.bigjeon.grumbling.data.Chat_Data;
import com.bigjeon.grumbling.data.Chat_Noti;
import com.bigjeon.grumbling.data.Notification_Data;
import com.bigjeon.grumbling.data.Post_Data;
import com.bumptech.glide.Glide;
import com.example.grumbling.R;
import com.example.grumbling.databinding.ActivityShowSelectedPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Show_Selected_Post_Activity extends AppCompatActivity {

    private static final String TAG = "확인 시바";

    private ActivityShowSelectedPostBinding binding;
    private Context mContext = Show_Selected_Post_Activity.this;
    private DatabaseReference DB;
    private FirebaseAuth mAuth;
    private String Post_Title;
    private String My_Email;
    private String My_Name;
    private String My_Uid;
    private String My_Img;
    private Boolean Favorite_State;
    private int Favorite_Count;
    private String Notification_Favorite_Key = "Add_Favorite";
    private Post_Data Post;
    private Chat_rcv_Adapter adapter;
    private ArrayList<Chat_Data> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_selected_post);
        binding.setSelectPostView(this);

        Intent Get_Post_Title = getIntent();
        if (Get_Post_Title == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                Post_Title = bundle.getString("TITLE");
            }
        }else{
            Post_Title = Get_Post_Title.getStringExtra("TITLE");
        }
        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference("Posts");

        Get_Selected_Posts();
        Set_My_Data();
        Get_Chatting_List();

        binding.SelectedPostToolbar.bringToFront();
        binding.SelectedPostUserImg.setOnClickListener(v -> Go_Selected_User_Profile());
        binding.SelectedPostFavoriteCircleCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteClicked(DB.child(Post.getPost_Title()));
                    if (Favorite_State == true){
                        binding.SelectedPostFavoriteCircleCIV.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.Theme_Text_Color));
                        binding.SelectedPostFavoriteCircleCIV.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                        Favorite_Count--;
                        Favorite_State = false;
                    }else{
                        binding.SelectedPostFavoriteCircleCIV.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.light_pink));
                        binding.SelectedPostFavoriteCircleCIV.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                        Favorite_Count++;
                        Favorite_State = true;
                    }
                binding.SelectedPostFavoriteCountTV.setText(Integer.toString(Favorite_Count));
            }
        });
        //채팅방으로 이동(아이템 뷰 클릭시 이동후 바로 답장으로 셋팅(Intent Filter이용))
        binding.SelectedPostEnterChattingCIV.setOnClickListener(v -> Go_Chat_Intent());
    }

    private void Go_Selected_User_Profile() {
        if (Post.getUser_Uid().equals(My_Uid)){
            Intent Go_View_My_Profile_Intent = new Intent(this, Setting_My_Profile_Activity.class);
            Go_View_My_Profile_Intent.putExtra("UID", Post.getUser_Uid());
            startActivity(Go_View_My_Profile_Intent);
        }else{
            Intent Go_View_User_Profile_Intent = new Intent(this, User_Profile_View_activity.class);
            Go_View_User_Profile_Intent.putExtra("UID", Post.getUser_Uid());
            startActivity(Go_View_User_Profile_Intent);
        }

    }

    private void Go_Chat_Intent(){
        Intent Go_Chat = new Intent(Show_Selected_Post_Activity.this, Chatting_Activity.class);
        Go_Chat.putExtra("TITLE", Post_Title);
        startActivity(Go_Chat);
    }
    private void Set_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Email = My_Data.getString("EMAIL", null);
        My_Name = My_Data.getString("NAME", null);
        My_Uid = My_Data.getString("UID", null);
        My_Img = My_Data.getString("IMG", null);
    }

    private void Get_Selected_Posts() {
        DB.addListenerForSingleValueEvent(new ValueEventListener() {
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
    private void onFavoriteClicked(DatabaseReference databaseReference){
        databaseReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @NotNull
            @Override
            public Transaction.Result doTransaction(@NonNull @NotNull MutableData currentData) {
                Post_Data data = currentData.getValue(Post_Data.class);
                if (data == null) {
                    return Transaction.success(currentData);
                }
                if (data.getFavorite().containsKey(mAuth.getCurrentUser().getUid())){
                    data.setFavorite_Count(data.getFavorite_Count() - 1);
                    data.getFavorite().remove(mAuth.getCurrentUser().getUid());
                }else{
                    data.setFavorite_Count(data.getFavorite_Count() + 1);
                    data.getFavorite().put(mAuth.getCurrentUser().getUid(), true);
                    Send_Noti_To_User(Post.getPost_Title(), Post.getUser_Uid());
                    Send_Favorite_Notification(data.getUser_Uid(), data.getPost_Title(), My_Uid);
                }
                currentData.setValue(data);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, boolean committed, @Nullable @org.jetbrains.annotations.Nullable DataSnapshot currentData) {

            }
        });

    }

    private void Data_Adjust(Post_Data post){
        if (My_Uid.equals(Post.getUser_Uid())){
            setSupportActionBar(binding.SelectedPostToolbar);
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("UID", Post.getUser_Uid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Picasso.get().load(document.getString("Img")).fit().into(binding.SelectedPostUserImg);
                        binding.SelectedPostUserName.setText(document.getString("Name"));
                        break;
                    }
                }
            }
        });
        Glide.with(Show_Selected_Post_Activity.this).load(Post.getPost_Background()).into(binding.SelectedPostBackground);
        binding.SelectedPostContent.setText(Post.getContent());
        binding.SelectedPostContent.setTextSize(Dimension.DP, Post.getContent_Text_Size());
        binding.SelectedPostContent.setTextColor(ContextCompat.getColor(this, Post.getContent_Text_Color()));
        binding.SelectedPostContent.setBackgroundColor(ContextCompat.getColor(this, Post.getContent_Back_Color()));
        if (post.getFavorite().containsKey(mAuth.getCurrentUser().getUid())){
            binding.SelectedPostFavoriteCircleCIV.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
            binding.SelectedPostFavoriteCircleCIV.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.light_pink));
            Favorite_Count = Post.getFavorite_Count();
            Favorite_State = true;
        }else {
            binding.SelectedPostFavoriteCircleCIV.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
            binding.SelectedPostFavoriteCircleCIV.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.Theme_Text_Color));
            Favorite_Count = Post.getFavorite_Count();
            Favorite_State = false;
        }
        if (Post.getFavorite_Count() < 1000){
            binding.SelectedPostFavoriteCountTV.setText(Integer.toString(Favorite_Count));
        }else{
            binding.SelectedPostFavoriteCountTV.setText("999+");
        }
        binding.ShowSelectedPostWhriteDateTv.setText(Change_Date(Post.getPost_Write_Date()));
    }

    private String Change_Date(String write_date){
        String new_writedate = "0000";
        try{
            SimpleDateFormat before = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat after = new SimpleDateFormat("yyyy.MM.dd hh:mm");
            Date dt_format = before.parse(write_date);
            new_writedate = after.format(dt_format);
            return new_writedate;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return new_writedate;
    }

    private void Get_Chatting_List(){
        adapter = new Chat_rcv_Adapter(list, My_Uid, this, false);
        binding.ChatListRcv.setAdapter(adapter);
        DatabaseReference Chat_DB = FirebaseDatabase.getInstance().getReference("Chats").child(Post_Title);
        Chat_DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    binding.ShowSelectedPostNoneChatTV.setVisibility(View.VISIBLE);
                }
                for (DataSnapshot data : snapshot.getChildren()){
                    Chat_Data chat = data.getValue(Chat_Data.class);
                    list.add(chat);
                    Log.d(TAG, list.toString());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this);
        binding.ChatListRcv.setLayoutManager(lm);
        binding.ChatListRcv.setHasFixedSize(true);
        binding.ChatListRcv.scrollToPosition(list.size() - 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post_context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Change_Post:
                Intent Go_Post_Write = new Intent(this, Post_Write_Activity.class);
                Go_Post_Write.putExtra("KEY", "CHANGE");
                Go_Post_Write.putExtra("TITLE", Post_Title);
                startActivity(Go_Post_Write);
                finish();
                break;
            case R.id.Delete_Post:
                DB = FirebaseDatabase.getInstance().getReference("Posts").child(Post_Title);
                DB.removeValue();
                DB = FirebaseDatabase.getInstance().getReference("Chats").child(Post_Title);
                if (DB != null){
                    DB.removeValue();
                }
                Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return true;
    }

    public void Send_Favorite_Notification(String UID, String Title, String My_Uid){
        if (!UID.equals(My_Uid)){
            SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String Send_Date = simpledate.format(date);

            Notification_Data Favorite_Noti = new Notification_Data(Notification_Favorite_Key, My_Uid, My_Name, My_Img, Send_Date, Title);
            DatabaseReference Other_Reference = FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Notifications").child("Post_Timepeed");
            Other_Reference.push().setValue(Favorite_Noti);
        }
    }

    private void Send_Noti_To_User(String Title, String User_Uid) {
        if (!User_Uid.equals(My_Uid)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("Token");
            reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    String User_Token = task.getResult().getValue().toString();
                    Model model = new Model(User_Token, null, new Data(My_Name + "님이 해당 게시글을 좋아합니다!", null, Title, ".Post", Title, Post.getPost_Background()));
                    Api apiService = ApiCLient.getClient().create(Api.class);
                    retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(model);

                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("서버 통신!!", "성공" + User_Token);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("서버 통신!!", "실패");
                        }
                    });
                }
            });
        }
    }
}