package com.bigjeon.grumbling.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.App_Main_Activity;
import com.bigjeon.grumbling.Chatting_Activity;
import com.bigjeon.grumbling.Google_Login_Activity;
import com.bigjeon.grumbling.Model.Api;
import com.bigjeon.grumbling.Model.ApiCLient;
import com.bigjeon.grumbling.Model.Data;
import com.bigjeon.grumbling.Model.Model;
import com.bigjeon.grumbling.Model.NotificationModel;
import com.bigjeon.grumbling.Post_Write_Activity;
import com.bigjeon.grumbling.Setting_My_Profile_Activity;
import com.bigjeon.grumbling.Show_Selected_Post_Activity;
import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.data.Chat_Data;
import com.bigjeon.grumbling.data.Chat_Noti;
import com.bigjeon.grumbling.data.Notification_Data;
import com.bigjeon.grumbling.data.Post_Data;
import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.grumbling.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Post_View_Rcv_Adapter extends RecyclerView.Adapter<Post_View_Rcv_Adapter.Holder>{
    private Context mContext;
    private String Get_Post_Key;
    private String My_Name;
    private String My_Img;
    private static final String TAG = "My_Check";
    private static final String Notification_Favorite_Key = "Add_Favorite";
    private String User_Uid;
    private String My_Uid;
    private int doubleclickFlag = 0;
    private int CLICK_DELAY = 300;
    private ArrayList<Post_Data> list = new ArrayList<>();
    //private Chat_rcv_Adapter chat_adapter;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference("Posts");
    public Post_View_Rcv_Adapter(Context context, ArrayList<Post_Data> list, String Key, String my_Uid, String my_name, String my_img, String user_uid){
        this.mContext = context;
        this.list = list;
        this.Get_Post_Key = Key;
        this.My_Uid = my_Uid;
        this.My_Name = my_name;
        this.My_Img = my_img;
        this.User_Uid = user_uid;
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
        if (data.getPost_Background() == null){
            Glide.with(holder.itemView).load(R.drawable.post_default_img).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.Post_Background_Img);
        }else{
            Glide.with(holder.itemView).load(data.getPost_Background()).into(holder.Post_Background_Img);
        }
        holder.WriteDate.setText(data.getPost_Write_Date());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("UID", data.getUser_Uid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        holder.User_Name.setText(document.get("Name").toString());
                        Picasso.get().load(document.getString("Img")).fit().into(holder.User_Img);
                        holder.Post_User_Location.setText("# " + document.get("Location").toString());
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
            holder.Favorite_Btn.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
            holder.Favorite_Btn.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.light_pink));
        }else {
            holder.Favorite_Btn.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
            holder.Favorite_Btn.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.Theme_Text_Color));
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

        //각포스팅별 채팅 목록을 불러옴
        ArrayList<Chat_Data> chat_dataArrayList = new ArrayList<Chat_Data>();
        Chat_rcv_Adapter chat_adapter = new Chat_rcv_Adapter(chat_dataArrayList, mAuth.getUid(), mContext, false);
        holder.Chatting_List_Rcv.setAdapter(chat_adapter);
        DatabaseReference Chat_DB = FirebaseDatabase.getInstance().getReference("Chats").child(data.getPost_Title());
        Chat_DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot data : snapshot.getChildren()){
                    count++;
                    if (data != null){
                        if (count == 1){
                            holder.Post_No_Chat_TV.setVisibility(View.GONE);
                        } else if (count >= 3){
                            holder.Expand_Civ.setVisibility(View.VISIBLE);
                        }
                        if (count == 8 ){
                            break;
                        }
                    }
                    Chat_Data chat = data.getValue(Chat_Data.class);
                    chat_dataArrayList.add(chat);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)holder.Chatting_List_Rcv.getLayoutParams();
                    layoutParams.height = 180;
                    holder.Chatting_List_Rcv.setLayoutParams(layoutParams);
                    chat_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(mContext);
        holder.Chatting_List_Rcv.setLayoutManager(lm);
        holder.Chatting_List_Rcv.setHasFixedSize(true);
        holder.Chatting_List_Rcv.scrollToPosition(list.size() - 1);

        holder.Favorite_Btn.setOnClickListener(v -> onFavoriteClicked(database.getReference().child("Posts").child(data.getPost_Title())));
        holder.Go_Chat_Room_Civ.setOnClickListener(v -> Go_Chat_Room(data));
        holder.Go_Chat_Room_Btn_In_Container.setOnClickListener(v -> Go_Chat_Room(data));
    }

    private String Change_Date(String write_date){
        String new_writedate = "0000";
        try{
            SimpleDateFormat before = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat after = new SimpleDateFormat("yyyy.MM.dd hh:mm");
            Date dt_format = before.parse(write_date);
            new_writedate = after.format(dt_format);
            return new_writedate;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return new_writedate;
    }

    public void Set_Grade(String Grade){
        Get_Post_Key = Grade;
    }

    private void Go_Chat_Room(Post_Data data){
        Intent Go_Chat = new Intent(mContext, Chatting_Activity.class);
        Go_Chat.putExtra("TITLE", data.getPost_Title());
        mContext.startActivity(Go_Chat);
    }

    private void Show_Selected_Post(Post_Data data) {
        if (data.getUser_Uid().equals(My_Uid) && Get_Post_Key.equals("유저 게시글")) {
            Intent Go_Show_Selected_Post = new Intent(mContext, Show_Selected_Post_Activity.class);
            Go_Show_Selected_Post.putExtra("TITLE", data.getPost_Title());
            mContext.startActivity(Go_Show_Selected_Post);
        }
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
        TextView WriteDate;
        RecyclerView Chatting_List_Rcv;
        ImageView Expand_Civ;
        CircleImageView Go_Chat_Room_Civ;
        TextView Post_User_Location;
        TextView Post_No_Chat_TV;
        Button Go_Chat_Room_Btn_In_Container;

        public Holder(@NonNull @NotNull View itemView) {
            super(itemView);
            User_Img = itemView.findViewById(R.id.Post_View_User_Img);
            User_Name = itemView.findViewById(R.id.Post_View_User_Name);
            Post_Content = itemView.findViewById(R.id.Post_View_Content);
            Post_Background_Img = itemView.findViewById(R.id.Post_View_Background);
            Favorite_Btn = itemView.findViewById(R.id.Post_View_Favorite_Circle_CIV);
            Favorite_Count = itemView.findViewById(R.id.Posting_Favorite_Count_TV);
            WriteDate = itemView.findViewById(R.id.Post_Write_Date_TV);
            Chatting_List_Rcv = itemView.findViewById(R.id.Chat_List_Rcv);
            Expand_Civ = itemView.findViewById(R.id.Post_View_Chat_Expand_Imv);
            Go_Chat_Room_Civ = itemView.findViewById(R.id.Go_Chat_Room_CIV);
            Post_User_Location = itemView.findViewById(R.id.Post_User_Location);
            Post_No_Chat_TV = itemView.findViewById(R.id.Post_View_No_Chat_TV);
            Go_Chat_Room_Btn_In_Container = itemView.findViewById(R.id.Post_View_Go_ChatRoom_Btn);

            //채팅의 갯수가 3개 이상이면 Expand버튼을 통해 Recyclerview의 크기를 변경(Wrap_Content로)
            Expand_Civ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Outside", "11");
                    if (Chatting_List_Rcv.getHeight() == 180){
                        Log.d("Inside", "21");
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)Chatting_List_Rcv.getLayoutParams();
                        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        Chatting_List_Rcv.setLayoutParams(layoutParams);
                        Expand_Civ.setBackgroundResource(R.drawable.ic_baseline_expand_less_24);
                    }else{
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)Chatting_List_Rcv.getLayoutParams();
                        layoutParams.height = 180;
                        Chatting_List_Rcv.setLayoutParams(layoutParams);
                        Expand_Civ.setBackgroundResource(R.drawable.ic_baseline_expand_more_24);
                    }
                }
            });

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
                    Send_Favorite_Notification(data.getUser_Uid(), data.getPost_Title(), mAuth.getCurrentUser().getUid(), data.getPost_Background());
                }
                currentData.setValue(data);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, boolean committed, @Nullable @org.jetbrains.annotations.Nullable DataSnapshot currentData) {

            }
        });
    }

    public void Send_Favorite_Notification(String UID, String Title, String My_Uid, String IMG){
        if (!UID.equals(My_Uid)){
            SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String Send_Date = simpledate.format(date);

            Notification_Data Favorite_Noti = new Notification_Data(Notification_Favorite_Key, My_Uid, My_Name, My_Img, Send_Date, Title);
            DatabaseReference Other_Reference = FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Notifications").child("Post_Timepeed");
            Other_Reference.push().setValue(Favorite_Noti);
            Send_Noti_To_User(Title, UID, IMG);
        }
    }
    //Child리스너 등록
    public void Get_Post_Child_Listener() {
        DB.addChildEventListener(Post_Child_Listener());
    }
    //Child리스너 해제
    public void Remove_Post_Child_Listener() {
        DB.removeEventListener(Post_Child_Listener());
    }

    public ChildEventListener Post_Child_Listener(){
            ChildEventListener Post_ChildListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                    Post_Data post = snapshot.getValue(Post_Data.class);
                    if (post.getUser_Uid().equals(mAuth.getCurrentUser().getUid())) {
                        Get_Post_Single();
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                    Post_Data post = snapshot.getValue(Post_Data.class);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getPost_Title().equals(post.getPost_Title())) {
                            list.set(i, post);
                            Log.d(TAG, "@@@@@@@@@@@@2" + list.get(i).getContent());
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                    Get_Post_Single();
                }

                @Override
                public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            };
            return Post_ChildListener;
    }

    public void Get_Post_Single() {
        if (Get_Post_Key.equals("유저 게시글")){
            DB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Post_Data post = data.getValue(Post_Data.class);
                        if (post.getUser_Uid().equals(User_Uid)){
                            list.add(0, post);
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }else if (Get_Post_Key.equals("모든 게시글")) {
            DB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Post_Data post = data.getValue(Post_Data.class);
                        post.setPost_Write_Date(Change_Date(post.getPost_Write_Date()));
                        list.add(0, post);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        } else {
            DB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Post_Data post = data.getValue(Post_Data.class);
                        if (post.getGrade().equals(Get_Post_Key)) list.add(0, post);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

    private void Send_Noti_To_User(String Title, String User_Uid, String IMG){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(User_Uid).child("Token");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String User_Token = task.getResult().getValue().toString();
                Model model = new Model(User_Token, null, new Data(My_Name + "님이 해당 게시글을 좋아합니다!", null, Title, ".Post", Title, IMG));
                Api apiService = ApiCLient.getClient().create(Api.class);
                retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(model);

                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("서버 통신!!", "성공" + User_Token);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("서버 통신!!", "실패");
                    }
                });
            }
        });
    }

}
