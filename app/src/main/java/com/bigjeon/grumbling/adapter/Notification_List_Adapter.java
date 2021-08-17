package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.Show_Selected_Post_Activity;
import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.data.Friend_Data;
import com.bigjeon.grumbling.data.Notification_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.NotificationItemBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Notification_List_Adapter extends RecyclerView.Adapter<Notification_List_Adapter.Notification_Holder> {
    private Context mContext;
    private NotificationItemBinding binding;
    private String My_Uid;
    private String Notification_Key_Friend = "Add_Friend";
    private String Notification_Key_Favorite = "Add_Favorite";
    private ArrayList<Notification_Data> Notification_list;

    public Notification_List_Adapter(Context mContext, ArrayList<Notification_Data> notification_data, String my_uid) {
        this.mContext = mContext;
        Notification_list = notification_data;
        this.My_Uid = my_uid;
    }

    @NonNull
    @NotNull
    @Override
    public Notification_Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        binding = NotificationItemBinding.inflate(LayoutInflater.from(context), parent, false);
        Notification_Holder holder = new Notification_Holder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Notification_Holder holder, int position) {
        if (Notification_list.get(position).getState().equals(Notification_Key_Friend)) {
            String User_Uid = Notification_list.get(position).getUid();
            holder.Send_Date.setText(Notification_list.get(position).getSend_Date());
            FirebaseFirestore Store = FirebaseFirestore.getInstance();
            Store.collection("Users").whereEqualTo("UID", Notification_list.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            holder.User_Name.setText(document.get("Name").toString() + "님이 친구추가 하였습니다.");
                            Picasso.get().load(document.getString("Img")).into(holder.User_Img);
                        }
                    }
                }
            });
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(Notification_list.get(position).getUid());
            db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.getResult().getValue() == null) {
                        holder.Accepted_Btn.setBackgroundResource(R.drawable.round_shape_off);
                        holder.Accepted_Btn.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.Transparent_Black80));
                        holder.Accepted_Btn.setVisibility(View.VISIBLE);
                        holder.Accepted_Btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                String Set_Friend_Date = simpledate.format(date);

                                Set_Friends(User_Uid);

                                Friend_Data My_Friend = new Friend_Data(User_Uid, Set_Friend_Date);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(User_Uid);
                                reference.setValue(My_Friend);

                                holder.Accepted_Btn.setText("친구");
                                holder.Accepted_Btn.setBackgroundResource(R.drawable.round_shape);
                                holder.Accepted_Btn.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.Transparent_Green));
                                notifyDataSetChanged();
                            }
                        });
                    } else {
                        holder.Accepted_Btn.setText("친구");
                        holder.Accepted_Btn.setBackgroundResource(R.drawable.round_shape);
                        holder.Accepted_Btn.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.Transparent_Green));
                        holder.Accepted_Btn.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else if (Notification_list.get(position).getState().equals(Notification_Key_Favorite)) {
            holder.Send_Date.setText(Notification_list.get(position).getSend_Date());
            holder.ItemView_Container.setOnClickListener(v -> Intent_To_Post(Notification_list.get(position).getState(), Notification_list.get(position).getPost_Title(), Notification_list.get(position).getUid()));
            FirebaseFirestore Store = FirebaseFirestore.getInstance();
            Store.collection("Users").whereEqualTo("UID", Notification_list.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            holder.User_Name.setText(document.get("Name").toString() + "님이 해당 게시글에 좋아요을 눌렀습니다.");
                            Picasso.get().load(document.getString("Img")).into(holder.User_Img);
                        }
                    }
                }
            });
        }
    }

    private void Intent_To_Post(String state, String TITLE, String UID) {
        if (state.equals(Notification_Key_Favorite)){
            Intent Go_Post_View_Intent = new Intent(mContext, Show_Selected_Post_Activity.class);
            Go_Post_View_Intent.putExtra("TITLE", TITLE);
            mContext.startActivity(Go_Post_View_Intent);
        }else{
            Intent Go_View_User_Profile_Intent = new Intent(mContext, User_Profile_View_activity.class);
            Go_View_User_Profile_Intent.putExtra("UID", UID);
            mContext.startActivity(Go_View_User_Profile_Intent);
        }
    }

    @Override
    public int getItemCount() {
        return Notification_list.size();
    }

    public class Notification_Holder extends RecyclerView.ViewHolder {
        public NotificationItemBinding binding;
        private RelativeLayout ItemView_Container;
        private TextView Send_Date;
        private TextView User_Name;
        private CircleImageView User_Img;
        private Button Accepted_Btn;

        public Notification_Holder(NotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            ItemView_Container = binding.NotificationContainer;
            User_Name = binding.NotificationUserName;
            User_Img = binding.NotificationUserImg;
            Accepted_Btn = binding.NotificationFriendAddBtn;
            Send_Date = binding.NotificationSendDate;
        }
    }
    private void Set_Friends(String UID){

        SimpleDateFormat simpledate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String Set_Friend_Date = simpledate.format(date);

        Friend_Data My_Friend = new Friend_Data(UID, Set_Friend_Date);
        Friend_Data Other_Friend = new Friend_Data(My_Uid, Set_Friend_Date);

        DatabaseReference My_Reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(UID);
        DatabaseReference Other_Reference = FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Friends").child(My_Uid);
        My_Reference.setValue(My_Friend);
        Other_Reference.setValue(Other_Friend);
    }

    public void Get_All_Notification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Notifications");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notification_Data Noti = data.getValue(Notification_Data.class);
                    if (!Noti.getUid().equals(My_Uid)){
                        Notification_list.add(0, Noti);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}
