package com.bigjeon.grumbling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bigjeon.grumbling.Model.Api;
import com.bigjeon.grumbling.Model.ApiCLient;
import com.bigjeon.grumbling.Model.Data;
import com.bigjeon.grumbling.Model.Model;
import com.bigjeon.grumbling.adapter.Chat_OnClickListener;
import com.bigjeon.grumbling.adapter.Chat_rcv_Adapter;
import com.bigjeon.grumbling.data.Chat_Data;
import com.bigjeon.grumbling.data.Chat_User_Uid_Data;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.Chat_Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chatting_Activity extends AppCompatActivity {
    Chat_Binding binding;
    private String Content;
    private String My_Uid;
    private String My_Name;
    private String Post_Title;
    private String Post_User_Uid;
    private String User_Token;
    private String My_Img;
    private String Reply_Target_Uid = "NONE";
    private String Reply_Target_Text = "NONE";
    private boolean First_Chat_Status = true;
    private String Chat_Id;
    private Chat_rcv_Adapter adapter;
    private ArrayList<Chat_Data> list = new ArrayList<>();

    private FirebaseDatabase DB;
    private DatabaseReference reference;
    private ValueEventListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatting);
        binding.setChatActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Intent Get_Data = getIntent();
        if (Get_Data == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                Post_Title = bundle.getString("TITLE");
            }
        }else{
            Post_Title = Get_Data.getStringExtra("TITLE");
        }
        Get_Post_User_Uid();
        SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Img = My_Data.getString("IMG", null);
        My_Name = My_Data.getString("NAME", null);

        //list뷰 설정
        adapter = new Chat_rcv_Adapter(list, My_Uid, this, false);
        binding.ChattingListListView.setAdapter(adapter);
        adapter.Set_Chat_rcv_Adapter(new Chat_OnClickListener() {
            @Override
            public void OnItemClicked(RecyclerView.ViewHolder Holder, View v, int pos) {
                if (list.get(pos).getText().contains("https:")){
                    Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(pos).getText()));
                    startActivity(browseIntent);
                }else{
                    binding.ReplidedEditContainer.setVisibility(View.VISIBLE);
                    binding.ReplyImg.setVisibility(View.VISIBLE);
                    Reply_Target_Uid = list.get(pos).getUid();
                    Reply_Target_Text = list.get(pos).getText();
                    binding.RepliedText.setText(Reply_Target_Text);
                    Get_Replied_Target_Name(Reply_Target_Uid);
                }
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this);
        binding.ChattingListListView.setLayoutManager(lm);
        binding.ChattingListListView.setHasFixedSize(true);

        binding.BackPressBtn.setOnClickListener( v -> onBackPressed());
        Set_My_State(true);
        DB = FirebaseDatabase.getInstance();
        reference = DB.getReference("Chats").child(Post_Title);
        reference.addValueEventListener(Regist_DB_Listener());

        binding.ChattingSendCIV.setOnClickListener(v -> Send_Message());

    }

    private void Get_Post_User_Uid(){
        reference = FirebaseDatabase.getInstance().getReference("Posts").child(Post_Title);
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Post_Data post = task.getResult().getValue(Post_Data.class);
                Post_User_Uid = post.getUser_Uid();
                Content = post.getContent();
                binding.ChattingPostContentTV.setText(Content);
            }
        });

    }

    private void Set_My_State(Boolean bool){
        if (bool == true){
            reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("State");
            reference.setValue(Post_Title);
        }else{
            reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("State");
            reference.setValue("NONE");
        }
    }

    private void Get_Replied_Target_Name(String Uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("UID", Uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        binding.RepliedTargetName.setText("@ : " + document.get("Name").toString() + "님 에게 답장");
                        break;
                    }
                }
            }
        });
    }

    private ValueEventListener Regist_DB_Listener(){
       listener = new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               list.clear();
               for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                   list.add(dataSnapshot.getValue(Chat_Data.class));
               }
               adapter.notifyDataSetChanged();
               binding.ChattingListListView.scrollToPosition(adapter.getItemCount() - 1);
           }

           @Override
           public void onCancelled(@NonNull @NotNull DatabaseError error) {

           }
       };
        return listener;
    }

    private void Send_Message(){
        String Message = binding.ChattingETV.getText().toString();
        if (Message.length() >= 1){
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd k:mm:ss:SSSS");
            String Time = simpleDateFormat.format(date);
            Chat_Id = Time + My_Uid;
            Chat_Data chat_data = new Chat_Data(My_Uid, Message, Time, Reply_Target_Text, Reply_Target_Uid, null, Chat_Id);
            reference = DB.getReference("Chats").child(Post_Title);
            reference.push().setValue(chat_data);
            binding.ChattingETV.setText("");
            if (binding.ReplidedEditContainer.getVisibility() == View.VISIBLE){
                Check_User_State(Message, Reply_Target_Uid);
            }else{
                Check_User_State(Message, Post_User_Uid);
            }
            binding.ReplidedEditContainer.setVisibility(View.GONE);
            binding.ReplyImg.setVisibility(View.GONE);
            Reply_Target_Uid = "NONE";
            Reply_Target_Text = "NONE";

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Set_My_State(false);
        reference.removeEventListener(listener);
    }

    @Override
    public void onBackPressed() {
        if (binding.ReplidedEditContainer.getVisibility() == View.VISIBLE){
            binding.ReplidedEditContainer.setVisibility(View.GONE);
            binding.ReplyImg.setVisibility(View.GONE);
            Reply_Target_Uid = "NONE";
            Reply_Target_Text = "NONE";
            binding.ChattingListListView.scrollToPosition(list.size() - 1);
        }else{
            super.onBackPressed();
        }
    }

    private void Send_Noti_To_User(String message, String User_Token, String tag){
        Model model = new Model(User_Token, null, new Data(My_Name + "님이 코멘트를 작성하였습니다.", message, tag, ".Chatting", Post_Title, My_Img));
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

    private void Check_User_State(String Message, String User_Uid){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("Token");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User_Token = task.getResult().getValue().toString();
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("State");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (Post_Title.equals(task.getResult().getValue()) || User_Uid.equals(My_Uid)){
                }else{
                    if (User_Uid.equals(Post_User_Uid)){
                        Send_Noti_To_User(Message, User_Token, Post_Title + "Chat");
                    }else{
                        Send_Noti_To_User(Message, User_Token, Post_Title + My_Uid + User_Uid);
                    }
                }
            }
        });
    }
}