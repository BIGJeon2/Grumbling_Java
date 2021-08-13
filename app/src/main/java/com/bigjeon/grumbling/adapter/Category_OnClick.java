package com.bigjeon.grumbling.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface Category_OnClick {
    public void OnItemClicked(RecyclerView.ViewHolder Holder, View v, int pos);
}
