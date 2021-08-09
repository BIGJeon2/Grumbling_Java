package com.bigjeon.grumbling.data;

public class Chat_Noti {
    private String User_Uid;

    public Chat_Noti(String user_Uid) {
        User_Uid = user_Uid;
    }

    public String getUser_Uid() {
        return User_Uid;
    }

    public void setUser_Uid(String user_Uid) {
        User_Uid = user_Uid;
    }
}
