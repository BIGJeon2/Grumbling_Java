package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.Show_Selected_Post_Activity;
import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.data.Friend_Data;
import com.bigjeon.grumbling.data.Notification_Data;
import com.example.grumbling.R;
import com.example.grumbling.databinding.TimepeedFriendsRequestItemBinding;
import com.example.grumbling.databinding.TimepeedPostItemBinding;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Timepeed_Rcv_Friends_Adapter extends RecyclerView.Adapter<Timepeed_Rcv_Friends_Adapter.Timepeed_Friends_ViewHolder> {
    private Context mContext;
    private ArrayList<Notification_Data> notification_list;
    private String My_Uid;
    private TimepeedFriendsRequestItemBinding binding;

    public Timepeed_Rcv_Friends_Adapter(Context mContext, ArrayList<Notification_Data> notification_list, String my_Uid) {
        this.mContext = mContext;
        this.notification_list = notification_list;
        My_Uid = my_Uid;
    }

    @NonNull
    @Override
    public Timepeed_Friends_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = TimepeedFriendsRequestItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        Timepeed_Friends_ViewHolder holder = new Timepeed_Friends_ViewHolder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Timepeed_Friends_ViewHolder holder, int position) {
        String User_Uid = notification_list.get(position).getUid();
        FirebaseFirestore Store = FirebaseFirestore.getInstance();
        Store.collection("Users").whereEqualTo("UID", notification_list.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        holder.Text.setText(document.get("Name").toString() + "님이 친구추가 하였습니다.");
                        Picasso.get().load(document.getString("Img")).into(holder.User_Img);
                    }
                }
            }
        });
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(notification_list.get(position).getUid());
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().getValue() == null) {
                    holder.Friend_Add_Btn.setBackgroundResource(R.drawable.round_shape_off);
                    holder.Friend_Add_Btn.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.Transparent_Black80));
                    holder.Friend_Add_Btn.setText("친구 추가");
                    holder.Friend_Add_Btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            String Set_Friend_Date = simpledate.format(date);

                            Set_Friends(User_Uid);

                            Friend_Data My_Friend = new Friend_Data(User_Uid, Set_Friend_Date);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(User_Uid);
                            reference.setValue(My_Friend);

                            holder.Friend_Add_Btn.setText("친구");
                            holder.Friend_Add_Btn.setBackgroundResource(R.drawable.round_shape);
                            holder.Friend_Add_Btn.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.Transparent_Green));
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    holder.Friend_Add_Btn.setText("친구");
                    holder.Friend_Add_Btn.setBackgroundResource(R.drawable.round_shape);
                    holder.Friend_Add_Btn.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.Transparent_Green));
                }
            }
        });

        holder.itemView.setOnClickListener(v -> Intent_To_User_Profile(User_Uid));
    }

    @Override
    public int getItemCount() {
        return notification_list.size();
    }

    public class Timepeed_Friends_ViewHolder extends RecyclerView.ViewHolder {
        public TimepeedFriendsRequestItemBinding binding;
        private CircleImageView User_Img;
        private TextView Text;
        private Button Friend_Add_Btn;
        public Timepeed_Friends_ViewHolder(TimepeedFriendsRequestItemBinding binding) {
            super(binding.getRoot());
            User_Img = binding.TimepeedFriendsUserCIV;
            Text = binding.TimepeedFriendsText;
            Friend_Add_Btn = binding.TimepeedFriendsAddBtn;
        }
    }

    public void Get_Friends_Timepeed(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Notifications").child("Firend_Timepeed");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notification_Data Noti = data.getValue(Notification_Data.class);
                    if (!Noti.getUid().equals(My_Uid)){
                        notification_list.add(0, Noti);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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

    private void Intent_To_User_Profile(String UID) {
            Intent Go_View_User_Profile_Intent = new Intent(mContext, User_Profile_View_activity.class);
            Go_View_User_Profile_Intent.putExtra("UID", UID);
            mContext.startActivity(Go_View_User_Profile_Intent);
    }

    public void List_Clear(){
     notification_list.clear();
    }
}
