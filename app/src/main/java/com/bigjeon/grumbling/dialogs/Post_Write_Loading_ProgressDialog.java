package com.bigjeon.grumbling.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

import com.example.grumbling.R;

public class Post_Write_Loading_ProgressDialog extends Dialog {
    public Post_Write_Loading_ProgressDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.post_write_loading_dialog);
    }
}
