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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Set_Post_Text_Fragment extends Fragment {

    private FragmentSetPostTextBinding binding;
    private int Posting_Content_BackColor = R.color.Transparent_Black30;
    private Color_Select_Rcv_Adapter Text_color_adapter;
    private Color_Select_Rcv_Adapter Back_color_adapter;
    private Post_Write_ViewModel VM;
    private List<Integer> Text_Colors;
    private List<Integer> Back_Colors;

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
        Back_Colors = new ArrayList<>(Arrays.asList(R.color.trans_dark_black, R.color.trans_light_Gray ,R.color.trans_light_purple ,R.color.trans_purple ,R.color.trans_light_pink, R.color.trans_pink, R.color.trans_light_red, R.color.trans_red, R.color.trans_light_blue, R.color.trans_blue, R.color.trans_blue_purple, R.color.trans_mint, R.color.trans_dark_mint, R.color.trans_light_white));

        Back_color_adapter = new Color_Select_Rcv_Adapter(getContext(), Back_Colors);
        binding.DialogPostingContentTextBackColorRcv.setAdapter(Back_color_adapter);
        Back_color_adapter.setOnClickListener(new Color_Adapter_OnClickListener() {
            @Override
            public void onItemClick(Color_Select_Rcv_Adapter.Holder_Color holder_color, View v, int pos) {
                Posting_Content_BackColor = Back_color_adapter.Get_Color(pos);
                VM.get_Post().getValue().setContent_Back_Color(Posting_Content_BackColor);
                VM.set_Post(VM.get_Post().getValue());
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(RecyclerView.HORIZONTAL);
        binding.DialogPostingContentTextBackColorRcv.setLayoutManager(lm);
        binding.DialogPostingContentTextBackColorRcv.setHasFixedSize(true);
        Back_color_adapter.notifyDataSetChanged();
    }

    private void Set_Content_Color() {
        Text_Colors = new ArrayList<>(Arrays.asList(R.color.dark_black, R.color.light_Gray ,R.color.light_purple ,R.color.purple ,R.color.light_pink, R.color.pink, R.color.light_red, R.color.red, R.color.light_blue, R.color.blue, R.color.blue_purple, R.color.mint, R.color.dark_mint, R.color.light_white));
        Text_color_adapter = new Color_Select_Rcv_Adapter(getContext(), Text_Colors);
        binding.DialogPostingContentTextColorRcv.setAdapter(Text_color_adapter);
        Text_color_adapter.setOnClickListener(new Color_Adapter_OnClickListener() {
            @Override
            public void onItemClick(Color_Select_Rcv_Adapter.Holder_Color holder_color, View v, int pos) {
                VM.get_Post().getValue().setContent_Text_Color(Text_color_adapter.Get_Color(pos));
                VM.set_Post(VM.get_Post().getValue());
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(RecyclerView.HORIZONTAL);
        binding.DialogPostingContentTextColorRcv.setLayoutManager(lm);
        binding.DialogPostingContentTextColorRcv.setHasFixedSize(true);
        Text_color_adapter.notifyDataSetChanged();
    }
}