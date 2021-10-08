package com.bigjeon.grumbling.data;

public class Notification_Data {
    private String State;
    private String Uid;
    private String User_Name;
    private String User_Img;
    private String Post_Title;
    private String Send_Date;

    public Notification_Data(){

    }

    public Notification_Data(String state, String uid, String user_Name, String user_Img, String send_Date, String post_Title) {
        State = state;
        Uid = uid;
        User_Name = user_Name;
        User_Img = user_Img;
        Send_Date = send_Date;
        Post_Title = post_Title;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
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

    public String getSend_Date() {
        return Send_Date;
    }

    public void setSend_Date(String send_Date) {
        Send_Date = send_Date;
    }

    public String getPost_Title() {
        return Post_Title;
    }

    public void setPost_Title(String post_Title) {
        Post_Title = post_Title;
    }
}
