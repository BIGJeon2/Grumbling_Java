package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.P2P_Chatting_Activity;
import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.data.Friend_Data;
import com.example.grumbling.databinding.UserListItemBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Friend_List_Adapter extends RecyclerView.Adapter<Friend_List_Adapter.Friend_ViewHolder> {
    private Context mContext;
    private UserListItemBinding binding;
    private String My_Uid;
    ArrayList<Friend_Data> Friends_List;

    public Friend_List_Adapter(Context mContext, String my_Uid, ArrayList<Friend_Data> friends_List) {
        this.mContext = mContext;
        My_Uid = my_Uid;
        Friends_List = friends_List;
    }

    @NonNull
    @NotNull
    @Override
    public Friend_ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        binding = UserListItemBinding.inflate(LayoutInflater.from(context), parent, false);
        Friend_ViewHolder holder = new Friend_ViewHolder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Friend_ViewHolder holder, int position) {
        FirebaseFirestore Store = FirebaseFirestore.getInstance();
        Store.collection("Users").whereEqualTo("UID", Friends_List.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        holder.User_Name.setText(document.get("Name").toString());
                        Picasso.get().load(document.getString("Img")).into(holder.User_Img);
                        holder.User_Location.setText("#" + document.getString("Location"));
                    }
                }
            }
        });
        holder.User_Img.setOnClickListener(v -> Go_User_Profile_View_Act(Friends_List.get(position).getUid()));
        holder.P2P_Chat_Civ.setOnClickListener(v -> Intent_To_P2P_Chatting(Friends_List.get(position).getUid()));
    }

    @Override
    public int getItemCount() {
        return Friends_List.size();
    }

    public class Friend_ViewHolder extends RecyclerView.ViewHolder {
        public UserListItemBinding binding;
        private RelativeLayout Container;
        private TextView User_Name;
        private CircleImageView User_Img;
        private TextView User_Location;
        private CircleImageView P2P_Chat_Civ;
        public Friend_ViewHolder(UserListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            Container = binding.UserListContainer;
            User_Name = binding.FriendListUserNameTV;
            User_Img = binding.FriendListUserImgCiv;
            User_Location = binding.FriendListUserUidTV;
            P2P_Chat_Civ = binding.UserListP2PChatCiv;
        }
    }
    private void Go_User_Profile_View_Act(String UID) {
            Intent Go_View_User_Profile_Intent = new Intent(mContext, User_Profile_View_activity.class);
            Go_View_User_Profile_Intent.putExtra("UID", UID);
            mContext.startActivity(Go_View_User_Profile_Intent);
    }

    private void Intent_To_P2P_Chatting(String User_Uid) {
        Intent GO_P2P_Chat = new Intent(mContext, P2P_Chatting_Activity.class);
        GO_P2P_Chat.putExtra("USER_UID", User_Uid);
        GO_P2P_Chat.putExtra("MY_UID", My_Uid);
        mContext.startActivity(GO_P2P_Chat);
    }
}
