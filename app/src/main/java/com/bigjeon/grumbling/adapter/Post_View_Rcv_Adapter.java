package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.data.Post_Data;
import com.bumptech.glide.Glide;
import com.example.grumbling.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Post_View_Rcv_Adapter extends RecyclerView.Adapter<Holder>{
    ArrayList<Post_Data> list;

    public Post_View_Rcv_Adapter(ArrayList<Post_Data> list){
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
        Picasso.get().load(data.getUser_Img()).into(holder.User_Img);
        holder.User_Name.setText(data.getUser_Name());
        holder.Post_Content.setText(data.getContent());
        holder.Post_Content.setTextSize(data.getContent_Text_Size());
        holder.Post_Content.setBackgroundColor(data.getContent_Back_Color());
        holder.Post_Content.setTextColor(data.getContent_Text_Color());
        Glide.with(holder.itemView).load(data.getPost_Background()).into(holder.Post_Background_Img);
        holder.Post_Write_Date.setText(data.getPost_Write_Date());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
class Holder extends RecyclerView.ViewHolder{
    CircleImageView User_Img;
    TextView User_Name;
    TextView Post_Content;
    ImageView Post_Background_Img;
    TextView Post_Write_Date;
    CircleImageView favorite_Btn;
    ImageButton Go_To_Comment_Btn;
    ImageView Declare_Btn;

    public Holder(@NonNull @NotNull View itemView) {
        super(itemView);
        User_Img = itemView.findViewById(R.id.Post_View_User_Img);
        User_Name = itemView.findViewById(R.id.Post_View_User_Name);
        Post_Content = itemView.findViewById(R.id.Post_View_Content);
        Post_Background_Img = itemView.findViewById(R.id.Post_View_Background);
        Post_Write_Date = itemView.findViewById(R.id.Post_View_WriteDate);
        favorite_Btn = itemView.findViewById(R.id.Post_View_Favorite_Circle_CIV);
        Go_To_Comment_Btn = itemView.findViewById(R.id.Post_View_Comment_Image_Btn);
        Declare_Btn = itemView.findViewById(R.id.Post_View_Declaration_Btn);
    }
}
