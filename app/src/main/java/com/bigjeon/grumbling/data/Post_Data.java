package com.bigjeon.grumbling.data;

import android.content.res.ColorStateList;

public class Post_Data {
    private String User_Name;
    private String User_Img;
    private String User_Uid;
    private String Content;
    private String Grade;

    public String getGrade() {
        return Grade;
    }

    public void setGrade(String grade) {
        Grade = grade;
    }

    private int Content_Text_Color;
    private int Content_Back_Color;
    private String Post_Write_Date;
    private int Post_Background;
    private int Favorite_Count;
    private int Declared_Count;

    public Post_Data(String user_Name, String user_Img, String user_Uid, String content, String grade, int content_Text_Color, int content_Back_Color, String post_Write_Date, int post_Background, int favorite_Count, int declared_Count) {
        User_Name = user_Name;
        User_Img = user_Img;
        User_Uid = user_Uid;
        Content = content;
        Grade = grade;
        Content_Text_Color = content_Text_Color;
        Content_Back_Color = content_Back_Color;
        Post_Write_Date = post_Write_Date;
        Post_Background = post_Background;
        Favorite_Count = favorite_Count;
        Declared_Count = declared_Count;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_Img() {
        return User_Img;
    }

    public void setUser_Img(String user_Img) {
        User_Img = user_Img;
    }

    public String getUser_Uid() {
        return User_Uid;
    }

    public void setUser_Uid(String user_Uid) {
        User_Uid = user_Uid;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getContent_Text_Color() {
        return Content_Text_Color;
    }

    public void setContent_Text_Color(int content_Text_Color) {
        Content_Text_Color = content_Text_Color;
    }

    public int getContent_Back_Color() {
        return Content_Back_Color;
    }

    public void setContent_Back_Color(int content_Back_Color) {
        Content_Back_Color = content_Back_Color;
    }

    public String getPost_Write_Date() {
        return Post_Write_Date;
    }

    public void setPost_Write_Date(String post_Write_Date) {
        Post_Write_Date = post_Write_Date;
    }

    public int getPost_Background() {
        return Post_Background;
    }

    public void setPost_Background(int post_Background) {
        Post_Background = post_Background;
    }

    public int getFavorite_Count() {
        return Favorite_Count;
    }

    public void setFavorite_Count(int favorite_Count) {
        Favorite_Count = favorite_Count;
    }

    public int getDeclared_Count() {
        return Declared_Count;
    }

    public void setDeclared_Count(int declared_Count) {
        Declared_Count = declared_Count;
    }
}
