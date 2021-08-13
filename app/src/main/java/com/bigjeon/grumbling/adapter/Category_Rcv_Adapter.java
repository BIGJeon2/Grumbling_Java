package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grumbling.databinding.CategoryItemBinding;

import java.util.ArrayList;

public class Category_Rcv_Adapter extends RecyclerView.Adapter<Category_Rcv_Adapter.Category_ViewHolder> implements Category_OnClick{

    private CategoryItemBinding binding;
    private ArrayList<String> Category_List = new ArrayList<>();
    private Context mcontext;
    private Category_OnClick listener;

    public Category_Rcv_Adapter(ArrayList<String> category_List, Context mcontext) {
        Category_List = category_List;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public Category_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        binding = CategoryItemBinding.inflate(LayoutInflater.from(context), parent, false);
        Category_Rcv_Adapter.Category_ViewHolder holder = new Category_Rcv_Adapter.Category_ViewHolder(binding, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Category_ViewHolder holder, int position) {
        holder.category.setText("#." + Category_List.get(position));
    }

    @Override
    public int getItemCount() {
        return Category_List.size();
    }

    public void setOnClickListener(Category_OnClick listener){
        this.listener = listener;
    }

    public String Get_Category(int position) {
        return Category_List.get(position);
    }

    @Override
    public void OnItemClicked(RecyclerView.ViewHolder Holder, View v, int pos) {
        if (listener != null){
            listener.OnItemClicked(Holder, v, pos);
        }
    }

    public class Category_ViewHolder extends RecyclerView.ViewHolder {
        private CategoryItemBinding binding;
        private TextView category;
        public Category_ViewHolder(@NonNull CategoryItemBinding binding, Category_OnClick listener) {
            super(binding.getRoot());
            this.binding = binding;
            category = binding.CategoryName;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null){
                        listener.OnItemClicked(Category_ViewHolder.this, v ,pos);
                    }
                }
            });
        }
    }
}
