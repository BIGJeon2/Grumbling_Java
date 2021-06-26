package com.bigjeon.grumbling.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.App_Main_Activity;
import com.bigjeon.grumbling.Show_Selected_Post_Activity;
import com.bigjeon.grumbling.data.Post_Data;
import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.bumptech.glide.Glide;
import com.example.grumbling.App_Main_Binding;
import com.example.grumbling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
    ArrayList<Post_Data> list;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    public Post_View_Rcv_Adapter(Context context, ArrayList<Post_Data> list){
        this.mContext = context;
        this.list = list;
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
        holder.User_Name.setText(data.getUser_Name());
        Picasso.get().load(data.getUser_Img()).into(holder.User_Img);
        holder.Post_Content.setText(data.getContent());
        holder.Post_Content.setTextSize(Dimension.DP, data.getContent_Text_Size());
        holder.Post_Content.setBackgroundColor(data.getContent_Back_Color());
        holder.Post_Content.setTextColor(data.getContent_Text_Color());
        Glide.with(holder.itemView).load(data.getPost_Background()).into(holder.Post_Background_Img);
        holder.Post_Write_Date.setText(DateChange(data.getPost_Write_Date()));
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
        holder.Favorite_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteClicked(database.getReference().child("Posts").child(data.getPost_Title()));
            }
        });

        holder.Post_Background_Img.setOnClickListener(v -> Show_Selected_Post(data));
    }

    private void Show_Selected_Post(Post_Data data) {
        Intent Go_Show_Selected_Post = new Intent(mContext, Show_Selected_Post_Activity.class);
        Go_Show_Selected_Post.putExtra("TITLE", data.getPost_Title());
        mContext.startActivity(Go_Show_Selected_Post);
    }

    private String DateChange(String date){
        SimpleDateFormat old_format = new SimpleDateFormat("yyyyMMddhhmmss");
        old_format.setTimeZone(TimeZone.getTimeZone("KST"));
        SimpleDateFormat new_format = new SimpleDateFormat("yy.MM.dd HH:mm");
        try {
            Date old_date = old_format.parse(date);
            String new_date = new_format.format(old_date);
            return new_date;

        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
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
        TextView Post_Write_Date;
        CircleImageView Favorite_Btn;
        TextView Favorite_Count;

        public Holder(@NonNull @NotNull View itemView) {
            super(itemView);
            User_Img = itemView.findViewById(R.id.Post_View_User_Img);
            User_Name = itemView.findViewById(R.id.Post_View_User_Name);
            Post_Content = itemView.findViewById(R.id.Post_View_Content);
            Post_Background_Img = itemView.findViewById(R.id.Post_View_Background);
            Post_Write_Date = itemView.findViewById(R.id.Post_View_WriteDate);
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
                }
                currentData.setValue(data);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, boolean committed, @Nullable @org.jetbrains.annotations.Nullable DataSnapshot currentData) {

            }
        });
    }
}
