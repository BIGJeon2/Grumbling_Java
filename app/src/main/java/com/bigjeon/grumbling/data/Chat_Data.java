package com.bigjeon.grumbling.data;

import java.util.HashMap;
import java.util.Map;

public class Chat_Data {
    private String Uid;
    private String Text;
    private String WriteDate;
    private String Reply_Target_Text;
    private String Reply_Target_User_Uid;
    private HashMap<String, Boolean> Read_Users;
    private String Chat_ID;

    public String getWriteDate() {
        return WriteDate;
    }

    public void setWriteDate(String writeDate) {
        WriteDate = writeDate;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getReply_Target_Text() {
        return Reply_Target_Text;
    }

    public void setReply_Target_Text(String reply_target_text) {
        Reply_Target_Text = reply_target_text;
    }

    public String getReply_Target_User_Uid() {
        return Reply_Target_User_Uid;
    }

    public void setReply_Target_User_Uid(String reply_Target_User_Uid) {
        Reply_Target_User_Uid = reply_Target_User_Uid;
    }

    public String getChat_ID() {
        return Chat_ID;
    }

    public void setChat_ID(String chat_ID) {
        Chat_ID = chat_ID;
    }

    public HashMap<String, Boolean> getRead_Users() {
        return Read_Users;
    }

    public void setRead_Users(HashMap<String, Boolean> read_Users) {
        Read_Users = read_Users;
    }

    public Chat_Data(){

    }

    public Chat_Data(String uid, String text, String writeDate, String reply_Target_Text, String reply_Target_User_Uid, HashMap<String, Boolean> read_Users, String chat_ID) {
        Uid = uid;
        Text = text;
        WriteDate = writeDate;
        Reply_Target_Text = reply_Target_Text;
        Reply_Target_User_Uid = reply_Target_User_Uid;
        Read_Users = read_Users;
        Chat_ID = chat_ID;
    }
}
