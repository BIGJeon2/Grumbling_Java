package com.bigjeon.grumbling.data;

public class Chat_User_Uid_Data {
    private String User_Uid;
    private String Chat_Room_Id;

    public Chat_User_Uid_Data(){

    }

    public Chat_User_Uid_Data(String user_Uid, String chat_Room_Id) {
        User_Uid = user_Uid;
        Chat_Room_Id = chat_Room_Id;
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
}
