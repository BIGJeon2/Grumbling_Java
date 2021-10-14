package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.P2P_Chatting_Activity;
import com.bigjeon.grumbling.Post_Write_Activity;
import com.bigjeon.grumbling.data.Chat_Data;
import com.bigjeon.grumbling.data.Chat_User_Uid_Data;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.databinding.ChattingListItemBinding;
import com.example.grumbling.databinding.UserListItemBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chatting_List_Rcv_Adapter extends RecyclerView.Adapter<Chatting_List_Rcv_Adapter.Chat_List_ViewHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mcontext;
    private DatabaseReference reference;
    private String My_Uid;
    private ArrayList<Chat_User_Uid_Data> Chatting_Room_List = new ArrayList<>();
    private ChattingListItemBinding binding;

    public Chatting_List_Rcv_Adapter(Context mcontext, String my_Uid, ArrayList<Chat_User_Uid_Data> chatting_Room_List) {
        this.mcontext = mcontext;
        My_Uid = my_Uid;
        Chatting_Room_List = chatting_Room_List;
    }

    @NonNull
    @NotNull
    @Override
    public Chat_List_ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        binding = ChattingListItemBinding.inflate(LayoutInflater.from(context), parent, false);
        Chatting_List_Rcv_Adapter.Chat_List_ViewHolder holder = new Chatting_List_Rcv_Adapter.Chat_List_ViewHolder(binding);
        holder.itemview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chatting_Room_List.get(holder.getAdapterPosition()).setNew_Chat_Count(0);
                Go_P2PChat(My_Uid, Chatting_Room_List.get(holder.getAdapterPosition()).getUser_Uid());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Chat_List_ViewHolder holder, int position) {
        Chat_User_Uid_Data room = Chatting_Room_List.get(position);
        db.collection("Users").whereEqualTo("UID", room.getUser_Uid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        holder.User_Name.setText(document.getString("Name"));
                        Picasso.get().load(document.getString("Img")).into(holder.User_Img);
                        holder.Last_Chat_Date.setText(room.getLast_Date());
                        holder.Last_Chat_Comment.setText(room.getLast_Content());
                        if (room.getNew_Chat_Count() != 0){
                            holder.New_Chat_Count.setText(Integer.toString(room.getNew_Chat_Count()));
                        }else{
                            holder.New_Chat_Count.setText("");
                        }
                    }
                }
            }
        });
    }
    public void Add_List(int position, Chat_User_Uid_Data Room){
        FirebaseDatabase.getInstance().getReference("Chat_Room").child(Room.getChat_Room_Id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int Count = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    Chat_Data chat = data.getValue(Chat_Data.class);
                    if (!chat.getRead_Users().containsKey(My_Uid)){
                        Count++;
                    }
                }
                Room.setNew_Chat_Count(Count);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        Chatting_Room_List.add(position, Room);
    }
    private void Go_P2PChat(String my_uid, String user_uid) {
        Intent Go_P2P_Chatting = new Intent(mcontext, P2P_Chatting_Activity.class);
        Go_P2P_Chatting.putExtra("USER_UID", user_uid);
        Go_P2P_Chatting.putExtra("MY_UID", my_uid);
        mcontext.startActivity(Go_P2P_Chatting);
    }
    @Override
    public int getItemCount() {
        return Chatting_Room_List.size();
    }

    public class Chat_List_ViewHolder extends RecyclerView.ViewHolder {
        private ChattingListItemBinding binding;
        private RelativeLayout itemview;
        private TextView Last_Chat_Date;
        private TextView Last_Chat_Comment;
        private TextView User_Name;
        private CircleImageView User_Img;
        private TextView New_Chat_Count;
        public Chat_List_ViewHolder(ChattingListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemview = binding.ChattingListContainer;
            Last_Chat_Date = binding.ChattingListLastChatDateTV;
            Last_Chat_Comment = binding.ChattingListLastChatTV;
            User_Name = binding.ChattingListUserNameTV;
            User_Img = binding.ChattingListUserImgCiv;
            New_Chat_Count = binding.ChattingListNewChatCountTV;

            itemview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mcontext);
                        dialog.setTitle("해당 채팅방을 나가시겠습니까?");
                        dialog.setMessage("해당 채팅방을 나가도 채팅 내역은 저장되어, 추후 재 확인이 가능합니다.");
                        dialog.setPositiveButton("나가기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference DB = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("My_Chatting_List").child(Chatting_Room_List.get(pos).getChat_Room_Id());
                                DB.removeValue();
                                Chatting_Room_List.remove(pos);
                                notifyDataSetChanged();
                            }
                        });
                        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                    }
                    return false;
                }
            });
        }
    }
}
