package com.bigjeon.grumbling.fragments;

import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bigjeon.grumbling.Post_Write_Activity;
import com.bigjeon.grumbling.adapter.Color_Adapter_OnClickListener;
import com.bigjeon.grumbling.adapter.Color_Select_Rcv_Adapter;
import com.bigjeon.grumbling.data.Post_Data;
import com.bigjeon.grumbling.factory.Post_Write_VM_Factory;
import com.bigjeon.grumbling.viewmodel.Post_Write_ViewModel;
import com.example.grumbling.R;
import com.example.grumbling.databinding.FragmentSetPostBackgroundBinding;
import com.example.grumbling.databinding.FragmentSetPostTextBinding;

public class Set_Post_Text_Fragment extends Fragment {

    private FragmentSetPostTextBinding binding;
    private int Posting_Content_BackColor = R.color.Transparent_Black40;
    private Color_Select_Rcv_Adapter color_adapter;
    private Post_Write_ViewModel VM;

    public Set_Post_Text_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set__post__text, container, false);
        View root = binding.getRoot();

        VM = new ViewModelProvider(requireActivity(), new Post_Write_VM_Factory()).get(Post_Write_ViewModel.class);

        Set_Content_Back_Color();
        Set_Content_Color();
        //글자 크기==============================================
        binding.DialogPostingContentTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                VM.get_Post().getValue().setContent_Text_Size(seekBar.getProgress());
                VM.set_Post(VM.get_Post().getValue());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                VM.get_Post().getValue().setContent_Text_Size(seekBar.getProgress());
                VM.set_Post(VM.get_Post().getValue());
            }
        });

        return root;
    }

    private void Set_Content_Back_Color() {
        color_adapter = new Color_Select_Rcv_Adapter(getContext());
        color_adapter.Set_Color_List(0);
        binding.DialogPostingContentTextBackColorRcv.setAdapter(color_adapter);
        color_adapter.setOnClickListener(new Color_Adapter_OnClickListener() {
            @Override
            public void onItemClick(Color_Select_Rcv_Adapter.Holder_Color holder_color, View v, int pos) {
                Posting_Content_BackColor = color_adapter.Get_Color(pos);
                VM.get_Post().getValue().setContent_Back_Color(Posting_Content_BackColor);
                VM.set_Post(VM.get_Post().getValue());
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(RecyclerView.HORIZONTAL);
        binding.DialogPostingContentTextBackColorRcv.setLayoutManager(lm);
        binding.DialogPostingContentTextBackColorRcv.setHasFixedSize(true);
        color_adapter.notifyDataSetChanged();
    }

    private void Set_Content_Color() {
        color_adapter = new Color_Select_Rcv_Adapter(getContext());
        color_adapter.Set_Color_List(0);
        binding.DialogPostingContentTextColorRcv.setAdapter(color_adapter);
        color_adapter.setOnClickListener(new Color_Adapter_OnClickListener() {
            @Override
            public void onItemClick(Color_Select_Rcv_Adapter.Holder_Color holder_color, View v, int pos) {
                VM.get_Post().getValue().setContent_Text_Color(color_adapter.Get_Color(pos));
                VM.set_Post(VM.get_Post().getValue());
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(RecyclerView.HORIZONTAL);
        binding.DialogPostingContentTextColorRcv.setLayoutManager(lm);
        binding.DialogPostingContentTextColorRcv.setHasFixedSize(true);
        color_adapter.notifyDataSetChanged();
    }
}