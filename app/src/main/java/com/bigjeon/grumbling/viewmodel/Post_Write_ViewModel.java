package com.bigjeon.grumbling.viewmodel;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.R;

import java.net.URI;
import java.util.HashMap;

public class Post_Write_ViewModel extends ViewModel {
    public MutableLiveData<Post_Data> Post = new MutableLiveData<>();
    public MutableLiveData<String> TITLE = new MutableLiveData<>();
    public MutableLiveData<String> UID = new MutableLiveData<>();
    public MutableLiveData<Uri> IMG_URI = new MutableLiveData<>();
    public MutableLiveData<String> IMG_String = new MutableLiveData<>();
    public MutableLiveData<String> CONTENT = new MutableLiveData<>();
    public MutableLiveData<String> GRADE = new MutableLiveData<>();
    public MutableLiveData<Integer> COTENT_SIZE = new MutableLiveData<>();
    public MutableLiveData<Integer> CONTENT_COLOR = new MutableLiveData<>();
    public MutableLiveData<Integer> FAVORITE_COUNT = new MutableLiveData<>();
    public MutableLiveData<String> WRITE_DATE = new MutableLiveData<>();
    public MutableLiveData<String> POST_BACK_IMG = new MutableLiveData<>();
    public MutableLiveData<HashMap<String, Boolean>> FAVORITE = new MutableLiveData<>();
    public MutableLiveData<Integer> DECLARED_COUNT = new MutableLiveData<>();
    public String IMG_Status = "String";

    public MutableLiveData<Post_Data> Get_Post(){
        if (Post == null){
            this.Post = new MutableLiveData<Post_Data>();
        }
        return Post;
    }

    public MutableLiveData<String> getTITLE() {
        return TITLE;
    }

    public void setTITLE(MutableLiveData<String> TITLE) {
        this.TITLE = TITLE;
    }

    public MutableLiveData<String> getUID() {
        return UID;
    }

    public void setUID(MutableLiveData<String> UID) {
        this.UID = UID;
    }

    public MutableLiveData<Uri> getIMG_URI() {
        return IMG_URI;
    }

    public void setIMG_URI(MutableLiveData<Uri> IMG_URI) {
        this.IMG_URI = IMG_URI;
    }

    public MutableLiveData<String> getIMG_String() {
        return IMG_String;
    }

    public void setIMG_String(MutableLiveData<String> IMG_String) {
        this.IMG_String = IMG_String;
    }

    public MutableLiveData<String> getCONTENT() {
        return CONTENT;
    }

    public void setCONTENT(MutableLiveData<String> CONTENT) {
        this.CONTENT = CONTENT;
    }

    public MutableLiveData<String> getGRADE() {
        return GRADE;
    }

    public void setGRADE(MutableLiveData<String> GRADE) {
        this.GRADE = GRADE;
    }

    public MutableLiveData<Integer> getCOTENT_SIZE() {
        return COTENT_SIZE;
    }

    public void setCOTENT_SIZE(MutableLiveData<Integer> COTENT_SIZE) {
        this.COTENT_SIZE = COTENT_SIZE;
    }

    public MutableLiveData<Integer> getCONTENT_COLOR() {
        return CONTENT_COLOR;
    }

    public void setCONTENT_COLOR(MutableLiveData<Integer> CONTENT_COLOR) {
        this.CONTENT_COLOR = CONTENT_COLOR;
    }

    public MutableLiveData<Integer> getFAVORITE_COUNT() {
        return FAVORITE_COUNT;
    }

    public void setFAVORITE_COUNT(MutableLiveData<Integer> FAVORITE_COUNT) {
        this.FAVORITE_COUNT = FAVORITE_COUNT;
    }

    public MutableLiveData<HashMap<String, Boolean>> getFAVORITE() {
        return FAVORITE;
    }

    public void setFAVORITE(MutableLiveData<HashMap<String, Boolean>> FAVORITE) {
        this.FAVORITE = FAVORITE;
    }

    public MutableLiveData<Integer> getDECLARED_COUNT() {
        return DECLARED_COUNT;
    }

    public void setDECLARED_COUNT(MutableLiveData<Integer> DECLARED_COUNT) {
        this.DECLARED_COUNT = DECLARED_COUNT;
    }

    public String getIMG_Status() {
        return IMG_Status;
    }

    public void setIMG_Status(String IMG_Status) {
        this.IMG_Status = IMG_Status;
    }

    public MutableLiveData<String> getWRITE_DATE() {
        return WRITE_DATE;
    }

    public void setWRITE_DATE(MutableLiveData<String> WRITE_DATE) {
        this.WRITE_DATE = WRITE_DATE;
    }

    public MutableLiveData<String> getPOST_BACK_IMG() {
        return POST_BACK_IMG;
    }

    public void setPOST_BACK_IMG(MutableLiveData<String> POST_BACK_IMG) {
        this.POST_BACK_IMG = POST_BACK_IMG;
    }
}
