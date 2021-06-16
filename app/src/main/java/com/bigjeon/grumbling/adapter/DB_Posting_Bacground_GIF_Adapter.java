package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.data.DB_Posting_Background_GIF;
import com.bumptech.glide.Glide;
import com.example.grumbling.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DB_Posting_Bacground_GIF_Adapter extends RecyclerView.Adapter<DB_Posting_Bacground_GIF_Adapter.Holder_Gif> implements Gif_OnClikListener {

    private ArrayList<DB_Posting_Background_GIF> Gif_List = new ArrayList<DB_Posting_Background_GIF>();

    Gif_OnClikListener listener;

    @NonNull
    @NotNull
    @Override
    public Holder_Gif onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.db_gif_img_item, parent, false);
        return new Holder_Gif(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder_Gif holder, int position) {
        DB_Posting_Background_GIF data = Gif_List.get(position);
        Glide.with(holder.itemView).load(data.getGIF()).into(holder.Gif_Img);
        holder.Gif_Name.setText(data.getGIF_Name());
    }

    @Override
    public int getItemCount() {
        return Gif_List.size();
    }

    public void setOnClickListener(Gif_OnClikListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClcick(Holder_Gif holder_gif, View view, int position) {
        if (listener != null){
            listener.onItemClcick(holder_gif, view, position);
        }
    }

    public class Holder_Gif extends RecyclerView.ViewHolder{
        CircleImageView Gif_Img;
        TextView Gif_Name;
        public Holder_Gif(@NonNull @NotNull View itemView, Gif_OnClikListener listener) {
            super(itemView);
            Gif_Img = itemView.findViewById(R.id.Gif_Circle_IMV);
            Gif_Name = itemView.findViewById(R.id.Gif_Name_TV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                        if (listener != null){
                            listener.onItemClcick(Holder_Gif.this, v, pos);
                        }
                }
            });
        }
    }
    public DB_Posting_Background_GIF Get_Gif(int position){
        return Gif_List.get(position);
    }
    public void Add_Gif(DB_Posting_Background_GIF item){
        Gif_List.add(item);
    }
}
