package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.data.Chat_Data;
import com.example.grumbling.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_rcv_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Chat_Data> Chat_Datas;
    String My_Uid;
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

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(mContext).inflate(R.layout.my_chatting_item, parent, false);
                viewHolder = new My_Chat_ViewHolder(view);
                break;
            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.other_chatting_item, parent, false);
                viewHolder = new Other_Chat_ViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof My_Chat_ViewHolder){
            ((My_Chat_ViewHolder)holder).Comment.setText(Chat_Datas.get(position).getText());
            ((My_Chat_ViewHolder)holder).Write_Date.setText(Chat_Datas.get(position).getWriteDate());
        }else{
            ((Other_Chat_ViewHolder)holder).Comment.setText(Chat_Datas.get(position).getText());
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").whereEqualTo("UID", Chat_Datas.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            ((Other_Chat_ViewHolder)holder).User_Name.setText(document.get("Name").toString());
                            Picasso.get().load(document.getString("Img")).into(((Other_Chat_ViewHolder)holder).User_Img);
                            ((Other_Chat_ViewHolder)holder).Comment.setText(Chat_Datas.get(position).getText());
                            ((Other_Chat_ViewHolder)holder).Write_Date.setText(Chat_Datas.get(position).getWriteDate());
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

    @Override
    public int getItemViewType(int position) {
        if (Chat_Datas.get(position).getUid().equals(My_Uid)){
            return 0;
        }else{
            return 1;
        }
    }

    private class My_Chat_ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView User_Img;
        TextView User_Name;
        TextView Write_Date;
        TextView Comment;
        public My_Chat_ViewHolder(View itemview) {
            super(itemview);
            User_Img = itemview.findViewById(R.id.Chatting_User_Img_CIV);
            User_Name = itemview.findViewById(R.id.Chatting_User_Name_TV);
            Write_Date = itemview.findViewById(R.id.Chatting_WriteDate_TV);
            Comment = itemview.findViewById(R.id.Chatting_Text_TV);
        }
    }

    private class Other_Chat_ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView User_Img;
        TextView User_Name;
        TextView Write_Date;
        TextView Comment;
        public Other_Chat_ViewHolder(View itemview) {
            super(itemview);
            User_Img = itemview.findViewById(R.id.Chatting_User_Img_CIV);
            User_Name = itemview.findViewById(R.id.Chatting_User_Name_TV);
            Write_Date = itemview.findViewById(R.id.Chatting_WriteDate_TV);
            Comment = itemview.findViewById(R.id.Chatting_Text_TV);
        }
    }
}
