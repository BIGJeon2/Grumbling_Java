package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.data.Chat_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.MyChattingItemBinding;
import com.example.grumbling.databinding.MyChattingReplyBinding;
import com.example.grumbling.databinding.OtherChattingItemBinding;
import com.example.grumbling.databinding.OtherChattingReplyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_rcv_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Chat_OnClickListener {
    private MyChattingItemBinding My_binding;
    private OtherChattingItemBinding Other_binding;
    private OtherChattingReplyBinding Other_Repling_binding;
    private MyChattingReplyBinding My_Repling_binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Chat_Data> Chat_Datas;
    String My_Uid;
    Chat_OnClickListener listener;
    private Context mContext;

    public Chat_rcv_Adapter(ArrayList<Chat_Data> chat_Datas, String my_Uid, Context mContext) {
        Chat_Datas = chat_Datas;
        My_Uid = my_Uid;
        this.mContext = mContext;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType){
            case 0:
                My_binding = MyChattingItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
                viewHolder = new My_Chat_ViewHolder(My_binding);
                break;
            case 1:
                My_Repling_binding = MyChattingReplyBinding.inflate(LayoutInflater.from(mContext), parent, false);
                viewHolder = new My_Repling_Chat_ViewHolder(My_Repling_binding);
                break;
            case 2:
                Other_binding = OtherChattingItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
                viewHolder = new Other_Chat_ViewHolder(Other_binding);
                break;
            case 3:
                Other_Repling_binding = OtherChattingReplyBinding.inflate(LayoutInflater.from(mContext), parent, false);
                viewHolder = new Other_Repling_Chat_ViewHolder(Other_Repling_binding);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof My_Chat_ViewHolder) {

            ((My_Chat_ViewHolder) holder).Comment.setText(Chat_Datas.get(position).getText());
            ((My_Chat_ViewHolder) holder).Write_Date.setText(Chat_Datas.get(position).getWriteDate());

        }else if (holder instanceof My_Repling_Chat_ViewHolder){

            ((My_Repling_Chat_ViewHolder) holder).Comment.setText(Chat_Datas.get(position).getText());
            ((My_Repling_Chat_ViewHolder) holder).Write_Date.setText(Chat_Datas.get(position).getWriteDate());
            ((My_Repling_Chat_ViewHolder) holder).Replied_Text.setText(Chat_Datas.get(position).getReply_Target_Text());
            db.collection("Users").whereEqualTo("UID", Chat_Datas.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ((My_Repling_Chat_ViewHolder) holder).Replied_User_Name.setText("@ : " + document.get("Name").toString() + "님 에게 답장");
                            break;
                        }
                    }
                }
            });
        } else if (holder instanceof Other_Chat_ViewHolder) {

            ((Other_Chat_ViewHolder) holder).Comment.setText(Chat_Datas.get(position).getText());
            ((Other_Chat_ViewHolder) holder).Write_Date.setText(Chat_Datas.get(position).getWriteDate());

            db.collection("Users").whereEqualTo("UID", Chat_Datas.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ((Other_Chat_ViewHolder) holder).User_Name.setText(document.get("Name").toString());
                            Picasso.get().load(document.getString("Img")).into(((Other_Chat_ViewHolder) holder).User_Img);
                            break;
                        }
                    }
                }
            });
        } else {
            ((Other_Repling_Chat_ViewHolder) holder).Comment.setText(Chat_Datas.get(position).getText());
            ((Other_Repling_Chat_ViewHolder) holder).Write_Date.setText(Chat_Datas.get(position).getWriteDate());
            ((Other_Repling_Chat_ViewHolder) holder).Replied_Text.setText(Chat_Datas.get(position).getReply_Target_Text());
            db.collection("Users").whereEqualTo("UID", Chat_Datas.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ((Other_Repling_Chat_ViewHolder) holder).User_Name.setText(document.get("Name").toString());
                            Picasso.get().load(document.getString("Img")).into(((Other_Repling_Chat_ViewHolder) holder).User_Img);
                            break;
                        }
                    }
                }
            });
            db.collection("Users").whereEqualTo("UID", Chat_Datas.get(position).getReply_Target_User_Uid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ((Other_Repling_Chat_ViewHolder) holder).Replied_User_Name.setText("@ : " + document.get("Name").toString() + "님 에게 답장");
                            break;
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return Chat_Datas.size();
    }
///////
    @Override
    public int getItemViewType(int position) {
        if (Chat_Datas.get(position).getUid().equals(My_Uid)){
            if (Chat_Datas.get(position).getReply_Target_User_Uid().equals("NONE")){
                return 0;
            }else {
                return 1;
            }
        }else{
            if (Chat_Datas.get(position).getReply_Target_User_Uid().equals("NONE")){
                return 2;
            }else {
                return 3;
            }
        }
    }

    public void Set_Chat_rcv_Adapter(Chat_OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void OnItemClicked(RecyclerView.ViewHolder Holder, View v, int pos) {
        if (listener != null){
            listener.OnItemClicked(Holder, v, pos);
        }
    }

    private class My_Chat_ViewHolder extends RecyclerView.ViewHolder {
        public MyChattingItemBinding binding;
        TextView Write_Date;
        TextView Comment;
        public My_Chat_ViewHolder(MyChattingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            Write_Date = binding.ChattingWriteDateTV;
            Comment = binding.ChattingTextTV;
            Comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null) {
                        listener.OnItemClicked(Chat_rcv_Adapter.My_Chat_ViewHolder.this, v, pos);
                    }
                }
            });
        }
    }

    private class My_Repling_Chat_ViewHolder extends RecyclerView.ViewHolder {
        public MyChattingReplyBinding binding;
        TextView Write_Date;
        TextView Comment;
        TextView Replied_User_Name;
        TextView Replied_Text;
        public My_Repling_Chat_ViewHolder(MyChattingReplyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            Write_Date = binding.ChattingWriteDateTV;
            Comment = binding.ChattingTextTV;
            Replied_Text = binding.RepliedChatTV;
            Replied_User_Name = binding.RepliedName;
            Comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null) {
                        listener.OnItemClicked(Chat_rcv_Adapter.My_Repling_Chat_ViewHolder.this, v, pos);
                    }
                }
            });
        }
    }

    private class Other_Chat_ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView User_Img;
        TextView User_Name;
        TextView Write_Date;
        TextView Comment;
        public Other_Chat_ViewHolder(OtherChattingItemBinding binding) {
            super(binding.getRoot());
            User_Img = binding.ChattingUserImgCIV;
            User_Name = binding.ChattingUserNameTV;
            Write_Date = binding.ChattingWriteDateTV;
            Comment = binding.ChattingTextTV;
            Comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null) {
                        listener.OnItemClicked(Chat_rcv_Adapter.Other_Chat_ViewHolder.this, v, pos);
                    }
                }
            });
        }
    }
    private class Other_Repling_Chat_ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView User_Img;
        TextView User_Name;
        TextView Write_Date;
        TextView Comment;
        TextView Replied_User_Name;
        TextView Replied_Text;
        public Other_Repling_Chat_ViewHolder(OtherChattingReplyBinding binding) {
            super(binding.getRoot());
            User_Img = binding.ChattingUserImgCIV;
            User_Name = binding.ChattingUserNameTV;
            Write_Date = binding.ChattingWriteDateTV;
            Comment = binding.ChattingTextTV;
            Replied_Text = binding.RepliedChatTV;
            Replied_User_Name = binding.RepliedName;
            Comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null) {
                        listener.OnItemClicked(Chat_rcv_Adapter.Other_Repling_Chat_ViewHolder.this, v, pos);
                    }
                }
            });
        }
    }
}
