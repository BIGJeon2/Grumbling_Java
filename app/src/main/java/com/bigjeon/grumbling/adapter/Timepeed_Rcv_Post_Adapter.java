package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.Show_Selected_Post_Activity;
import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.data.Notification_Data;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Timepeed_Rcv_Post_Adapter extends RecyclerView.Adapter<Timepeed_Rcv_Post_Adapter.Timepeed_Post_ViewHolder> {
    private Context mContext;
    private ArrayList<Notification_Data> notification_list;
    private String My_Uid;
    private TimepeedPostItemBinding binding;

    public Timepeed_Rcv_Post_Adapter(Context mContext, ArrayList<Notification_Data> notification_list, String my_Uid) {
        this.mContext = mContext;
        this.notification_list = notification_list;
        My_Uid = my_Uid;
    }

    @NonNull
    @Override
    public Timepeed_Rcv_Post_Adapter.Timepeed_Post_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = TimepeedPostItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        Timepeed_Post_ViewHolder holder = new Timepeed_Post_ViewHolder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Timepeed_Rcv_Post_Adapter.Timepeed_Post_ViewHolder holder, int position) {
        String Title = notification_list.get(position).getPost_Title();
        FirebaseFirestore Store = FirebaseFirestore.getInstance();
        Store.collection("Users").whereEqualTo("UID", notification_list.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        holder.Text.setText(document.get("Name").toString() + "님이 해당 게시글에 좋아요를 눌렀습니다.");
                        Picasso.get().load(document.getString("Img")).into(holder.User_Img);
                    }
                }
            }
        });
        holder.Date.setText(notification_list.get(position).getSend_Date());
        holder.itemView.setOnClickListener(v -> Intent_To_Post(Title));
    }

    @Override
    public int getItemCount() {
        return notification_list.size();
    }

    public class Timepeed_Post_ViewHolder extends RecyclerView.ViewHolder {
        public TimepeedPostItemBinding binding;
        private CircleImageView User_Img;
        private TextView Text;
        private TextView Date;
        public Timepeed_Post_ViewHolder(TimepeedPostItemBinding binding) {
            super(binding.getRoot());
            User_Img = binding.TimepeedPostUserCIV;
            Text = binding.TimepeedPostText;
            Date = binding.TimepeedPostSendTime;
        }
    }

    public void Get_Post_Timepeed(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Notifications").child("Post_Timepeed");

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

    private void Intent_To_Post(String TITLE) {
            Intent Go_Post_View_Intent = new Intent(mContext, Show_Selected_Post_Activity.class);
            Go_Post_View_Intent.putExtra("TITLE", TITLE);
            mContext.startActivity(Go_Post_View_Intent);
    }

    public void List_Clear(){
        notification_list.clear();
    }

}
