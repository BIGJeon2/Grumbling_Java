package com.bigjeon.grumbling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bigjeon.grumbling.adapter.Chat_OnClickListener;
import com.bigjeon.grumbling.adapter.Chat_rcv_Adapter;
import com.bigjeon.grumbling.data.Chat_Data;
import com.bigjeon.grumbling.data.Chat_User_Uid_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.P2P_Chat_Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class P2P_Chatting_Activity extends AppCompatActivity {
    P2P_Chat_Binding binding;
    private String My_Uid;
    private String User_Uid;
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
    private ChildEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_p2p_chatting);
        binding.setP2PChatActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //인테트를 통해 유저 UID, 내 UID 받아옴
        Intent Get_Data = getIntent();
        My_Uid = Get_Data.getStringExtra("MY_UID");
        User_Uid = Get_Data.getStringExtra("USER_UID");

        Get_User_Data();
        Set_Chatting_Room_ID();
        Check_Chatting_State();

        //list뷰 설정
        adapter = new Chat_rcv_Adapter(list, My_Uid, this);
        binding.ChattingListListView.setAdapter(adapter);
        adapter.Set_Chat_rcv_Adapter(new Chat_OnClickListener() {
            @Override
            public void OnItemClicked(RecyclerView.ViewHolder Holder, View v, int pos) {
                binding.ReplidedEditContainer.setVisibility(View.VISIBLE);
                Reply_Target_Uid = list.get(pos).getUid();
                Reply_Target_Text = list.get(pos).getText();
                binding.RepliedText.setText(Reply_Target_Text);
                Toast.makeText(P2P_Chatting_Activity.this, Reply_Target_Uid, Toast.LENGTH_SHORT).show();
                Get_Replied_Target_Name(Reply_Target_Uid);
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        binding.ChattingListListView.setLayoutManager(lm);
        binding.ChattingListListView.setHasFixedSize(true);
        binding.ChattingListListView.setNestedScrollingEnabled(false);
        binding.ChattingListListView.scrollToPosition(0);

        DB = FirebaseDatabase.getInstance();
        reference = DB.getReference("Chat_Room").child(Chatting_Room_ID);
        reference.addChildEventListener(Regist_DB_Listener());

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

    private ChildEventListener Regist_DB_Listener(){
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Chat_Data chat_data = snapshot.getValue(Chat_Data.class);
                list.add(chat_data);
                adapter.notifyDataSetChanged();
                binding.ChattingListListView.scrollToPosition(list.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

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
            Calendar calendar = Calendar.getInstance();
            String Time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            Chat_Id = Time + My_Uid;
            Chat_Data chat_data = new Chat_Data(My_Uid, Message, Time, Reply_Target_Text, Reply_Target_Uid, Chat_Id);
            reference.push().setValue(chat_data);

            binding.ChattingETV.setText("");
            binding.ReplidedEditContainer.setVisibility(View.GONE);
            Reply_Target_Uid = "NONE";
            Reply_Target_Text = "NONE";

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.removeEventListener(listener);
    }

    @Override
    public void onBackPressed() {
        if (binding.ReplidedEditContainer.getVisibility() == View.VISIBLE){
            binding.ReplidedEditContainer.setVisibility(View.GONE);
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
        Add_Chatting_Room_To_Profile();
    }

    private void Add_Chatting_Room_To_Profile(){
        if (First_Chat_Status == true){
            Chat_User_Uid_Data My_data = new Chat_User_Uid_Data(User_Uid, Chatting_Room_ID);
            reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("My_Chatting_List").child(Chatting_Room_ID);
            reference.setValue(My_data);
            Chat_User_Uid_Data User_data = new Chat_User_Uid_Data(My_Uid, Chatting_Room_ID);
            reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("My_Chatting_List").child(Chatting_Room_ID);
            reference.setValue(User_data);

            First_Chat_Status = false;
        }
    }
}