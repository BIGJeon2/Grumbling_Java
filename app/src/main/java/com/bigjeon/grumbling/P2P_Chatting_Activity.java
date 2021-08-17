package com.bigjeon.grumbling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bigjeon.grumbling.Model.Api;
import com.bigjeon.grumbling.Model.ApiCLient;
import com.bigjeon.grumbling.Model.Data;
import com.bigjeon.grumbling.Model.Model;
import com.bigjeon.grumbling.Model.NotificationModel;
import com.bigjeon.grumbling.adapter.Chat_OnClickListener;
import com.bigjeon.grumbling.adapter.Chat_rcv_Adapter;
import com.bigjeon.grumbling.data.Chat_Data;
import com.bigjeon.grumbling.data.Chat_Noti;
import com.bigjeon.grumbling.data.Chat_User_Uid_Data;
import com.bigjeon.grumbling.data.Notification_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.P2P_Chat_Binding;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.firebase.firestore.auth.Token;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class P2P_Chatting_Activity extends AppCompatActivity {
    P2P_Chat_Binding binding;
    private String My_Uid;
    private String My_Name;
    private String My_Img;
    private String User_Uid;
    private String User_Token;
    private Boolean User_State;
    private String Chatting_Room_ID;
    private String Reply_Target_Uid = "NONE";
    private String Reply_Target_Text = "NONE";
    private boolean First_Chat_Status = true;
    private String Chat_Id;
    private Chat_rcv_Adapter adapter;
    private ArrayList<Chat_Data> list = new ArrayList<>();

    private FirebaseDatabase DB;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference reference;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_p2p_chatting);
        binding.setP2PChatActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //인테트, Sharepreference를 통해 유저 UID, 내 UID 받아옴
        SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Img = My_Data.getString("IMG", null);

        Intent Get_Data = getIntent();
        if (Get_Data == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
               User_Uid = bundle.getString("tag");
            }
        }else{
            User_Uid = Get_Data.getStringExtra("USER_UID");
        }

        binding.BackPressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        My_Name = getSharedPreferences("My_Data", MODE_PRIVATE).getString("NAME", null);

        Get_User_Data();
        Set_Chatting_Room_ID();
        Check_Chatting_State();
        Set_User_State(My_Uid, true);
        //list뷰 설정
        adapter = new Chat_rcv_Adapter(list, My_Uid, this, true);
        binding.ChattingListListView.setAdapter(adapter);
        adapter.Set_Chat_rcv_Adapter(new Chat_OnClickListener() {
            @Override
            public void OnItemClicked(RecyclerView.ViewHolder Holder, View v, int pos) {
                binding.ReplidedEditContainer.setVisibility(View.VISIBLE);
                binding.ReplyImg.setVisibility(View.VISIBLE);
                Reply_Target_Uid = list.get(pos).getUid();
                Reply_Target_Text = list.get(pos).getText();
                binding.RepliedText.setText(Reply_Target_Text);
                Get_Replied_Target_Name(Reply_Target_Uid);
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this);
        binding.ChattingListListView.setLayoutManager(lm);
        binding.ChattingListListView.setHasFixedSize(true);

        DB = FirebaseDatabase.getInstance();
        reference = DB.getReference("Chat_Room").child(Chatting_Room_ID);
        reference.addValueEventListener(Regist_DB_Listener());

        binding.ChattingSendCIV.setOnClickListener(v -> Send_Message());

    }

    private void Get_User_Data() {
        db.collection("Users").whereEqualTo("UID", User_Uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        binding.ChattingPostContentTV.setText(document.getString("Name"));
                        break;
                    }
                }
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("Token");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User_Token = task.getResult().getValue().toString();
            }
        });

    }

    private void Set_User_State(String My_Uid, Boolean state){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("State");
        if (state == true){
            reference.setValue(Chatting_Room_ID);
        }else{
            reference.setValue("NONE");
        }

    }

    private void Get_Replied_Target_Name(String Uid) {
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
            Map<String, Object> Read_Users = new HashMap<>();
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat_Data data = dataSnapshot.getValue(Chat_Data.class);
                    if (!data.getUid().equals(My_Uid) && !data.getRead_Users().containsKey(My_Uid)){
                        data.getRead_Users().put(My_Uid, true);
                        reference.child(data.getChat_ID()).setValue(data);
                    }
                    list.add(data);
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
//            Calendar calendar = Calendar.getInstance();
//            String Time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            Chat_Id = Time + My_Uid;
            HashMap<String, Boolean> Read_Users = new HashMap<>();
            Read_Users.put(My_Uid, true);
            Chat_Data chat_data = new Chat_Data(My_Uid, Message, Time, Reply_Target_Text, Reply_Target_Uid, Read_Users, Chat_Id);
            Add_Chatting_Room_To_Profile(Time, Message);
            reference = DB.getReference("Chat_Room").child(Chatting_Room_ID).child(Chat_Id);
            reference.setValue(chat_data);
            if (Check_User_State() == false){
                Send_Noti_To_User(Message);
            }
            binding.ChattingETV.setText("");
            binding.ReplidedEditContainer.setVisibility(View.GONE);
            binding.ReplyImg.setVisibility(View.GONE);
            Reply_Target_Uid = "NONE";
            Reply_Target_Text = "NONE";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Set_User_State(My_Uid, false);
        reference.removeEventListener(listener);
    }

    @Override
    public void onBackPressed() {
        if (binding.ReplidedEditContainer.getVisibility() == View.VISIBLE){
            binding.ReplidedEditContainer.setVisibility(View.GONE);
            binding.ReplyImg.setVisibility(View.GONE);
            Reply_Target_Uid = "NONE";
            Reply_Target_Text = "NONE";
        }else{
            super.onBackPressed();
        }
    }

    private void Set_Chatting_Room_ID(){
        ArrayList<String> UID = new ArrayList<>();
        UID.add(0, My_Uid);
        UID.add(1, User_Uid);

        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };

        Collections.sort(UID, comparator);

        Chatting_Room_ID = UID.get(0) + UID.get(1);
    }

    private void Check_Chatting_State(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("My_Chatting_List").child(Chatting_Room_ID);
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    First_Chat_Status = true;
                }else{
                    Chat_User_Uid_Data chat_data = task.getResult().getValue(Chat_User_Uid_Data.class);
                    if (chat_data == null){
                        First_Chat_Status = true;
                    }else{
                        First_Chat_Status = false;
                    }
                }
            }
        });
    }

    private void Add_Chatting_Room_To_Profile(String Last_Chat, String Write_Date){
            Chat_User_Uid_Data My_data = new Chat_User_Uid_Data(User_Uid, Chatting_Room_ID, Last_Chat, Write_Date, 0);
            reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("My_Chatting_List").child(Chatting_Room_ID);
            reference.setValue(My_data);
            Chat_User_Uid_Data User_data = new Chat_User_Uid_Data(My_Uid, Chatting_Room_ID, Last_Chat, Write_Date, 0);
            reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("My_Chatting_List").child(Chatting_Room_ID);
            reference.setValue(User_data);
            First_Chat_Status = false;
        }

        private void Send_Noti_To_User(String message){
            Model model = new Model(User_Token, null, new Data(My_Name + "님이 메세지를 보냈습니다.", message, Chatting_Room_ID, ".P2P", My_Uid, My_Img));
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

        private Boolean Check_User_State(){
            reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("State");
            reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task == null){
                        User_State = false;
                    }else if (Chatting_Room_ID.equals(task.getResult().toString())){
                        User_State = true;
                    }else{
                        User_State = false;
                    }
                }
            });
            return User_State;
        }
}