package com.bigjeon.grumbling.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface Chat_OnClickListener {
    public void OnItemClicked(RecyclerView.ViewHolder Holder, View v, int pos);
}
