package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.Show_Selected_Post_Activity;
import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.data.Notification_Data;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.databinding.TimepeedPostItemBinding;
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
        Notification_Data data = notification_list.get(position);
        FirebaseFirestore Store = FirebaseFirestore.getInstance();
        Store.collection("Users").whereEqualTo("UID", data.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        holder.Text.setText("'" + document.get("Name").toString() + "' 님이 회원님의 게시글을 좋아합니다.");
                        Picasso.get().load(document.getString("Img")).fit().into(holder.User_Img);
                    }
                }
            }
        });
        holder.itemView.setOnClickListener(v -> Intent_To_Post(data.getPost_Title()));
    }

    @Override
    public int getItemCount() {
        return notification_list.size();
    }

    public class Timepeed_Post_ViewHolder extends RecyclerView.ViewHolder {
        public TimepeedPostItemBinding binding;
        private CircleImageView User_Img;
        private TextView Text;
        public Timepeed_Post_ViewHolder(TimepeedPostItemBinding binding) {
            super(binding.getRoot());
            User_Img = binding.TimepeedPostUserCIV;
            Text = binding.TimepeedPostText;
        }
    }

    private void Intent_To_Post(String TITLE) {
            Intent Go_Post_View_Intent = new Intent(mContext, Show_Selected_Post_Activity.class);
            Go_Post_View_Intent.putExtra("TITLE", TITLE);
            mContext.startActivity(Go_Post_View_Intent);
    }

}
