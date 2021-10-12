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
        Notification_Data data = notification_list.get(position);
        FirebaseFirestore Store = FirebaseFirestore.getInstance();
        Store.collection("Users").whereEqualTo("UID", data.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        holder.Text.setText("'" + document.get("Name").toString() + "' 님이 회원님을 친구추가 하였습니다.");
                        Picasso.get().load(document.getString("Img")).into(holder.User_Img);
                    }
                }
            }
        });
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(data.getUid());
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().getValue() == null) {
                    holder.Friend_Add_Btn.setBackgroundResource(R.drawable.ic_baseline_group_add_24);
                    holder.Friend_Add_Btn.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.Transparent_Green));
                    holder.Friend_Add_Btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            String Set_Friend_Date = simpledate.format(date);

                            Set_Friends(data.getUid());

                            Friend_Data My_Friend = new Friend_Data(data.getUid(), Set_Friend_Date);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(data.getUid());
                            reference.setValue(My_Friend);

                            holder.Friend_Add_Btn.setBackgroundResource(R.drawable.ic_baseline_group_24);
                            holder.Friend_Add_Btn.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.Theme_Less_Accent_Color));
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    holder.Friend_Add_Btn.setBackgroundResource(R.drawable.ic_baseline_group_24);
                    holder.Friend_Add_Btn.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.Theme_Less_Accent_Color));
                }
            }
        });

        holder.itemView.setOnClickListener(v -> Intent_To_User_Profile(data.getUid()));
    }

    @Override
    public int getItemCount() {
        return notification_list.size();
    }

    public class Timepeed_Friends_ViewHolder extends RecyclerView.ViewHolder {
        public TimepeedFriendsRequestItemBinding binding;
        private CircleImageView User_Img;
        private TextView Text;
        private CircleImageView Friend_Add_Btn;
        public Timepeed_Friends_ViewHolder(TimepeedFriendsRequestItemBinding binding) {
            super(binding.getRoot());
            User_Img = binding.TimepeedFriendsUserCIV;
            Text = binding.TimepeedFriendsText;
            Friend_Add_Btn = binding.TimepeedFriendsAddBtn;
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

    private void Intent_To_User_Profile(String UID) {
            Intent Go_View_User_Profile_Intent = new Intent(mContext, User_Profile_View_activity.class);
            Go_View_User_Profile_Intent.putExtra("UID", UID);
            mContext.startActivity(Go_View_User_Profile_Intent);
    }

}
