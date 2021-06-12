package com.bigjeon.grumbling.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.grumbling.R;

public class Post_Dialog extends Dialog {

    private Context context;

    public Post_Dialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setContentView(R.layout.dialog_posting);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);


    }
}
