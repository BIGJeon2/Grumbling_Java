package com.bigjeon.grumbling.data;

public class Chat_User_Uid_Data {
    private String User_Uid;
    private String Chat_Room_Id;
    private String Last_Date;

    public Chat_User_Uid_Data(){

    }

    public Chat_User_Uid_Data(String user_Uid, String chat_Room_Id, String last_Date) {
        User_Uid = user_Uid;
        Chat_Room_Id = chat_Room_Id;
        Last_Date = last_Date;
    }

    public String getUser_Uid() {
        return User_Uid;
    }

    public void setUser_Uid(String user_Uid) {
        User_Uid = user_Uid;
    }

    public String getChat_Room_Id() {
        return Chat_Room_Id;
    }

    public void setChat_Room_Id(String chat_Room_Id) {
        Chat_Room_Id = chat_Room_Id;
    }

    public String getLast_Date() {
        return Last_Date;
    }

    public void setLast_Date(String last_Date) {
        Last_Date = last_Date;
    }
}
