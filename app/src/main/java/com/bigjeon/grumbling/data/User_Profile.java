package com.bigjeon.grumbling.data;

import android.net.Uri;

import java.util.ArrayList;

public class User_Profile {
    private String User_Uid;
    private String User_Name;
    private Uri User_Img;
    private ArrayList<String> User_Favorites;
    private ArrayList<String> User_Friends;
    private ArrayList<String> User_Comment;

    public User_Profile(String my_uid, String my_name, String my_img, Object o, Object o1, Object o2) {
    }

    public String getUser_Uid() {
        return User_Uid;
    }

    public void setUser_Uid(String user_Uid) {
        User_Uid = user_Uid;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public Uri getUser_Img() {
        return User_Img;
    }

    public void setUser_Img(Uri user_Img) {
        User_Img = user_Img;
    }

    public ArrayList<String> getUser_Favorites() {
        return User_Favorites;
    }

    public void setUser_Favorites(ArrayList<String> user_Favorites) {
        User_Favorites = user_Favorites;
    }

    public ArrayList<String> getUser_Friends() {
        return User_Friends;
    }

    public void setUser_Friends(ArrayList<String> user_Friends) {
        User_Friends = user_Friends;
    }

    public ArrayList<String> getUser_Comment() {
        return User_Comment;
    }

    public void setUser_Comment(ArrayList<String> user_Comment) {
        User_Comment = user_Comment;
    }
}