package com.bigjeon.grumbling.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class Post_Write_ViewModel extends ViewModel {
    public MutableLiveData<Post_Data> Post = new MutableLiveData<>();
    public MutableLiveData<Uri> IMG_URI = new MutableLiveData<>();
    public MutableLiveData<String> IMG_String = new MutableLiveData<>();
    public MutableLiveData<String> IMG_State = new MutableLiveData<>();

    public LiveData<Post_Data> get_Post(){
        return Post;
    }
    public void set_Post(Post_Data post){
        this.Post.setValue(post);
    }

    public LiveData<Uri> getIMG_URI() {
        return IMG_URI;
    }

    public void setIMG_URI(Uri IMG_URI) {
        this.IMG_URI.setValue(IMG_URI);
    }

    public LiveData<String> getIMG_String() {
        return IMG_String;
    }

    public void setIMG_String(String IMG_String) {
      this.IMG_String.setValue(IMG_String);
    }

    public LiveData<String> getIMG_State() {
        return IMG_State;
    }

    public void setIMG_State(String IMG_State) {
        this.IMG_State.setValue(IMG_State);
    }

}
