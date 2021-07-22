package com.bigjeon.grumbling.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bigjeon.grumbling.Post_Write_Activity;
import com.bigjeon.grumbling.adapter.DB_Posting_Bacground_GIF_Adapter;
import com.bigjeon.grumbling.adapter.Gif_OnClikListener;
import com.bigjeon.grumbling.data.DB_Posting_Background_GIF;
import com.bigjeon.grumbling.factory.Post_Write_VM_Factory;
import com.bigjeon.grumbling.viewmodel.Post_Write_ViewModel;
import com.bumptech.glide.Glide;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentSetPostBackgroundBinding;
import com.squareup.picasso.Picasso;

public class Set_Post_Background_Fragment extends Fragment {

    private FragmentSetPostBackgroundBinding binding;
    private Post_Write_ViewModel VM;
    private DB_Posting_Bacground_GIF_Adapter adapter;

    public Set_Post_Background_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set__post__background, container, false);
        View root = binding.getRoot();

        VM = new ViewModelProvider(this, new Post_Write_VM_Factory()).get(Post_Write_ViewModel.class);

        Get_Gif_In_raw();
        binding.DialogPostingBackgroundGallery.setOnClickListener(v -> Get_Img_In_Gallery());
        return root;
    }

    private void Get_Gif_In_raw() {
        adapter = new DB_Posting_Bacground_GIF_Adapter();
        adapter.Add_Gif(new DB_Posting_Background_GIF("https://firebasestorage.googleapis.com/v0/b/grumber-9d1b9.appspot.com/o/Background_Images_GIF%2FTwinkleLED.gif?alt=media&token=8f32a89f-63cd-4927-bc93-a542af5be96d", "1번 배경"));
        adapter.Add_Gif(new DB_Posting_Background_GIF("https://firebasestorage.googleapis.com/v0/b/grumber-9d1b9.appspot.com/o/Background_Images_GIF%2Fmulti_colred_motion.gif?alt=media&token=f08c3e46-fa42-4a9d-9773-a6b491106a49", "2번 배경"));

        binding.DialogPostingBackgroundDBRcv.setAdapter(adapter);
        adapter.setOnClickListener(new Gif_OnClikListener() {
            @Override
            public void onItemClcick(DB_Posting_Bacground_GIF_Adapter.Holder_Gif holder_gif, View view, int position) {
                VM.setIMG_Status("STRING");
                VM.IMG_String.setValue(adapter.Get_Gif(position).getGIF());
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(RecyclerView.HORIZONTAL);
        binding.DialogPostingBackgroundDBRcv.setAdapter(adapter);
        binding.DialogPostingBackgroundDBRcv.setLayoutManager(lm);
        binding.DialogPostingBackgroundDBRcv.setHasFixedSize(true);
    }

    //갤러리 사진 가져오기
    private void Get_Img_In_Gallery() {
        Intent Get_Img = new Intent(Intent.ACTION_PICK);
        Get_Img.setType("image/*");
        mGetContent.launch("image/*");
    }
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    VM.IMG_URI.setValue(result);
                    VM.setIMG_Status("URI");
                }
            });

}