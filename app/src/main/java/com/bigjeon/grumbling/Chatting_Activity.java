package com.bigjeon.grumbling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.bigjeon.grumbling.adapter.Chat_rcv_Adapter;
import com.bigjeon.grumbling.data.Chat_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.Chat_Binding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

public class Chatting_Activity extends AppCompatActivity {
    Chat_Binding binding;
    private String Content;
    private String My_Uid;
    private String Post_Title;
    private Chat_rcv_Adapter adapter;
    private ArrayList<Chat_Data> list = new ArrayList<>();

    private FirebaseDatabase DB;
    private DatabaseReference reference;
    private ChildEventListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatting);
        binding.setChatActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Intent Get_Data = getIntent();
        Content = Get_Data.getStringExtra("CONTENT");
        My_Uid = Get_Data.getStringExtra("UID");
        Post_Title = Get_Data.getStringExtra("TITLE");
        binding.ChattingPostContentTV.setText(Content);

        //list뷰 설정
        adapter = new Chat_rcv_Adapter(list, My_Uid, this);
        binding.ChattingListListView.setAdapter(adapter);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        binding.ChattingListListView.setLayoutManager(lm);
        binding.ChattingListListView.setHasFixedSize(true);

        DB = FirebaseDatabase.getInstance();
        reference = DB.getReference("Chats");
        reference.child(Post_Title).addChildEventListener(Regist_DB_Listener());

        binding.ChattingSendCIV.setOnClickListener(v -> Send_Message());

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

            Chat_Data chat_data = new Chat_Data(My_Uid, Message, Time);
            reference.child(Post_Title).push().setValue(chat_data);

            binding.ChattingETV.setText("");

//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.removeEventListener(listener);
    }
}