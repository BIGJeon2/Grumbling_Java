package com.bigjeon.grumbling.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bigjeon.grumbling.data.Post_Data;

public class Post_Write_ViewModel extends ViewModel {
    private MutableLiveData<Post_Data> Post;

    public void Set_Post(MutableLiveData<Post_Data> post){
        this.Post = post;
    }
    public MutableLiveData<Post_Data> Get_Post(){
        if (Post == null){
            Post = new MutableLiveData<Post_Data>();
        }
        return Post;
    }
}
