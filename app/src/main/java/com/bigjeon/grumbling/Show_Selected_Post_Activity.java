package com.bigjeon.grumbling;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bigjeon.grumbling.data.Post_Data;
import com.bumptech.glide.Glide;
import com.example.grumbling.R;
import com.example.grumbling.databinding.ActivityShowSelectedPostBinding;
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
import java.util.Date;
import java.util.TimeZone;

public class Show_Selected_Post_Activity extends AppCompatActivity {

    ActivityShowSelectedPostBinding binding;
    private Context mContext = Show_Selected_Post_Activity.this;
    private DatabaseReference DB;
    private FirebaseAuth mAuth;
    private String Post_Title;
    private String My_Email;
    private String My_Name;
    private String My_Uid;
    private String My_Img;
    private Boolean Favorite_State;
    private int Favorite_Count;
    private Post_Data Post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_selected_post);
        binding.setSelectPostView(this);

        Intent Get_Post_Title = getIntent();
        Post_Title = Get_Post_Title.getStringExtra("TITLE");
        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference("Posts");

        Get_Selected_Posts();
        Set_My_Data();
//        binding.SelectedPostMyUserImgCiv.setOnClickListener(v -> Go_Selected_User_Profile());
        binding.SelectedPostFavoriteCircleCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteClicked(DB.child(Post.getPost_Title()));
                    if (Favorite_State == true){
                        binding.SelectedPostFavoriteCircleCIV.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        Favorite_Count--;
                        Favorite_State = false;
                    }else{
                        binding.SelectedPostFavoriteCircleCIV.setImageResource(R.drawable.ic_baseline_favorite_24);
                        Favorite_Count++;
                        Favorite_State = true;
                    }
                binding.SelectedPostFavoriteCountTV.setText(Integer.toString(Favorite_Count));
            }
        });
    }
    private void Set_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", Context.MODE_PRIVATE);
        My_Email = My_Data.getString("EMAIL", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
        Picasso.get().load(My_Img).into(binding.SelectedPostMyUserImgCiv);
    }

    private void Get_Selected_Posts() {
        DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    String Title = data.getValue(Post_Data.class).getPost_Title();
                    if (Title.equals(Post_Title)){
                        Post = data.getValue(Post_Data.class);
                        Data_Adjust(Post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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

    private void Data_Adjust(Post_Data post){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("UID", Post.getUser_Uid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Picasso.get().load(document.getString("Img")).into(binding.SelectedPostUserImg);
                        binding.SelectedPostUserName.setText(document.getString("Name"));
                        break;
                    }
                }
            }
        });
        Glide.with(Show_Selected_Post_Activity.this).load(Post.getPost_Background()).into(binding.SelectedPostBackground);
        binding.SelectedPostWriteDate.setText(DateChange(Post.getPost_Write_Date()));
        binding.SelectedPostContent.setText(Post.getContent());
        binding.SelectedPostContent.setTextSize(Dimension.DP, Post.getContent_Text_Size());
        binding.SelectedPostContent.setTextColor(ContextCompat.getColor(this, Post.getContent_Text_Color()));
        binding.SelectedPostContent.setBackgroundColor(ContextCompat.getColor(this, Post.getContent_Back_Color()));
        if (post.getFavorite().containsKey(mAuth.getCurrentUser().getUid())){
            binding.SelectedPostFavoriteCircleCIV.setImageResource(R.drawable.ic_baseline_favorite_24);
            Favorite_Count = Post.getFavorite_Count();
            Favorite_State = true;
        }else {
            binding.SelectedPostFavoriteCircleCIV.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            Favorite_Count = Post.getFavorite_Count();
            Favorite_State = false;
        }
        if (Post.getFavorite_Count() < 1000){
            binding.SelectedPostFavoriteCountTV.setText(Integer.toString(Favorite_Count));
        }else{
            binding.SelectedPostFavoriteCountTV.setText("999+");
        }
    }
}