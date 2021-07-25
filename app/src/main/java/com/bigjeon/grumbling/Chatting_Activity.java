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

import java.util.ArrayList;
import java.util.Calendar;

public class Chatting_Activity extends AppCompatActivity {
    Chat_Binding binding;
    private String Content;
    private String My_Uid;
    private String Post_Title;
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
        Content = Get_Data.getStringExtra("CONTENT");
        My_Uid = Get_Data.getStringExtra("UID");
        Post_Title = Get_Data.getStringExtra("TITLE");
        binding.ChattingPostContentTV.setText(Content);

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
                Toast.makeText(Chatting_Activity.this, Reply_Target_Uid, Toast.LENGTH_SHORT).show();
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
        reference = DB.getReference("Chats").child(Post_Title);
        reference.addValueEventListener(Regist_DB_Listener());

        binding.ChattingSendCIV.setOnClickListener(v -> Send_Message());

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
               binding.ChattingListListView.smoothScrollToPosition(adapter.getItemCount() - 1);
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
            binding.ChattingListListView.scrollToPosition(list.size() - 1);
        }else{
            super.onBackPressed();
        }
    }
}