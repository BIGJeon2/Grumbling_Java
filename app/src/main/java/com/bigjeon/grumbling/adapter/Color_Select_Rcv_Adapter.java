package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grumbling.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Color_Select_Rcv_Adapter extends RecyclerView.Adapter<Color_Select_Rcv_Adapter.Holder_Color> implements Color_Adapter_OnClickListener {
    private Context context;
    private List<Integer> Colors;
    Color_Adapter_OnClickListener listener;

    public Color_Select_Rcv_Adapter(Context context, List<Integer> colors) {
        this.context = context;
        Colors = colors;
    }

    @NonNull
    @NotNull
    @Override
    public Holder_Color onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.color_item, parent, false);
        return new Holder_Color(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder_Color holder, int position) {
        int Color = Colors.get(position);
        holder.Color_View.setColorFilter(ContextCompat.getColor(context, Color));
    }

    @Override
    public int getItemCount() {
        return Colors.size();
    }

    public void setOnClickListener(Color_Adapter_OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(Holder_Color holder_color, View v, int pos) {
        if (listener != null) {
            listener.onItemClick(holder_color, v, pos);
        }
    }

    public class Holder_Color extends RecyclerView.ViewHolder {
        CircleImageView Color_View;

        public Holder_Color(@NonNull @NotNull View itemView, Color_Adapter_OnClickListener listener) {
            super(itemView);
            Color_View = itemView.findViewById(R.id.Text_Color_Circle_CIV);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(Holder_Color.this, v, pos);
                    }
                }
            });
        }
    }

    public int Get_Color(int position) {
        return Colors.get(position);
    }

}
