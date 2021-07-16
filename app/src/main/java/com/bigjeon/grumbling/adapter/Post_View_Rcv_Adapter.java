package com.bigjeon.grumbling.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.App_Main_Activity;
import com.bigjeon.grumbling.Google_Login_Activity;
import com.bigjeon.grumbling.Setting_My_Profile_Activity;
import com.bigjeon.grumbling.Show_Selected_Post_Activity;
import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.data.Notification_Data;
import com.bigjeon.grumbling.data.Post_Data;
import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.bumptech.glide.Glide;
import com.example.grumbling.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class Post_View_Rcv_Adapter extends RecyclerView.Adapter<Post_View_Rcv_Adapter.Holder>{
    private Context mContext;
    private String Get_Post_Key;
    private static final String TAG = "My_Check";
    private static final String Notification_Favorite_Key = "Add_Favorite";
    private int doubleclickFlag = 0;
    private int CLICK_DELAY = 300;
    ArrayList<Post_Data> list;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference("Posts");
    public Post_View_Rcv_Adapter(Context context, ArrayList<Post_Data> list, String Key){
        this.mContext = context;
        this.list = list;
        this.Get_Post_Key = Key;
    }

    @NonNull
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.post_view_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        Post_Data data = list.get(position);
        holder.Post_Content.setText(data.getContent());
        holder.Post_Content.setTextSize(Dimension.DP, data.getContent_Text_Size());
        holder.Post_Content.setBackgroundColor(ContextCompat.getColor(mContext, data.getContent_Back_Color()));
        holder.Post_Content.setTextColor(ContextCompat.getColor(mContext, data.getContent_Text_Color()));
        Glide.with(holder.itemView).load(data.getPost_Background()).into(holder.Post_Background_Img);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("UID", data.getUser_Uid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        holder.User_Name.setText(document.get("Name").toString());
                        Picasso.get().load(document.getString("Img")).into(holder.User_Img);
                        break;
                    }
                }
            }
        });
        if (data.getFavorite_Count() < 1000){
            holder.Favorite_Count.setText(Integer.toString(data.getFavorite_Count()));
        }else{
            holder.Favorite_Count.setText("999+");
        }
        if (data.getFavorite().containsKey(mAuth.getCurrentUser().getUid())){
            holder.Favorite_Btn.setImageResource(R.drawable.ic_baseline_favorite_24);
        }else {
            holder.Favorite_Btn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
        holder.Post_Background_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doubleclickFlag++;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (doubleclickFlag == 1){
                            Show_Selected_Post(data);
                        }else if (doubleclickFlag == 2){
                            onFavoriteClicked(database.getReference().child("Posts").child(data.getPost_Title()));
                        }
                        doubleclickFlag = 0;
                    }
                }, 200);
            }
        });
        holder.User_Img.setOnClickListener(v -> Go_User_Profile_View_Act(data.getUser_Uid()));
    }

    private void Show_Selected_Post(Post_Data data) {
        Intent Go_Show_Selected_Post = new Intent(mContext, Show_Selected_Post_Activity.class);
        Go_Show_Selected_Post.putExtra("TITLE", data.getPost_Title());
        mContext.startActivity(Go_Show_Selected_Post);
    }

    private void Go_User_Profile_View_Act(String UID) {
        if (UID.equals(mAuth.getUid())){
            Intent Go_View_My_Profile_Intent = new Intent(mContext, Setting_My_Profile_Activity.class);
            Go_View_My_Profile_Intent.putExtra("UID", UID);
            mContext.startActivity(Go_View_My_Profile_Intent);
        }else{
            Intent Go_View_User_Profile_Intent = new Intent(mContext, User_Profile_View_activity.class);
            Go_View_User_Profile_Intent.putExtra("UID", UID);
            mContext.startActivity(Go_View_User_Profile_Intent);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView User_Name;
        CircleImageView User_Img;
        TextView Post_Content;
        ImageView Post_Background_Img;
        CircleImageView Favorite_Btn;
        TextView Favorite_Count;

        public Holder(@NonNull @NotNull View itemView) {
            super(itemView);
            User_Img = itemView.findViewById(R.id.Post_View_User_Img);
            User_Name = itemView.findViewById(R.id.Post_View_User_Name);
            Post_Content = itemView.findViewById(R.id.Post_View_Content);
            Post_Background_Img = itemView.findViewById(R.id.Post_View_Background);
            Favorite_Btn = itemView.findViewById(R.id.Post_View_Favorite_Circle_CIV);
            Favorite_Count = itemView.findViewById(R.id.Posting_Favorite_Count_TV);
        }
    }

    private void onFavoriteClicked(DatabaseReference databaseReference){
        databaseReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @NotNull
            @Override
            public Transaction.Result doTransaction(@NonNull @NotNull MutableData currentData) {
                Post_Data data = currentData.getValue(Post_Data.class);
                if (data == null) {
                    return Transaction.success(currentData);
                }
                if (data.getFavorite().containsKey(mAuth.getCurrentUser().getUid())){
                    data.setFavorite_Count(data.getFavorite_Count() - 1);
                    data.getFavorite().remove(mAuth.getCurrentUser().getUid());
                }else{
                    data.setFavorite_Count(data.getFavorite_Count() + 1);
                    data.getFavorite().put(mAuth.getCurrentUser().getUid(), true);
                    Send_Favorite_Notification(data.getUser_Uid(), data.getPost_Title(), mAuth.getCurrentUser().getUid());
                }
                currentData.setValue(data);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, boolean committed, @Nullable @org.jetbrains.annotations.Nullable DataSnapshot currentData) {

            }
        });
    }

    public void Send_Favorite_Notification(String UID, String Title, String My_Uid){

        SimpleDateFormat simpledate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String Send_Date = simpledate.format(date);

        Notification_Data Favorite_Noti = new Notification_Data(Notification_Favorite_Key, My_Uid, Send_Date, Title);
        DatabaseReference Other_Reference = FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Notifications");
        Other_Reference.push().setValue(Favorite_Noti);
    }

    public void Get_Post_Child_Listener(){
        DB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Post_Data post = snapshot.getValue(Post_Data.class);
                if (post.getUser_Uid().equals(mAuth.getCurrentUser().getUid())){
                    Get_Post_Single();
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Post_Data post = snapshot.getValue(Post_Data.class);
                for (int i = 0; i < list.size(); i++){
                    if (list.get(i).getPost_Title().equals(post.getPost_Title())){
                        list.set(i, post);
                        Log.d(TAG, "@@@@@@@@@@@@2" + list.get(i).getContent());
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                Post_Data post = snapshot.getValue(Post_Data.class);
                for (int i = 0; i < list.size(); i++){
                    if (list.get(i).getPost_Title().equals(post.getPost_Title())){
                        list.set(i, post);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void Get_Post_Single(){
        if (Get_Post_Key.equals("모든 게시글")) {
            DB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Post_Data post = data.getValue(Post_Data.class);
                        if (post.getGrade().equals("모든 사용자")) list.add(0, post);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }
}
