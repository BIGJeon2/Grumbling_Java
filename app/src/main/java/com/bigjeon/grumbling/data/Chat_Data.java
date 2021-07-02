package com.bigjeon.grumbling.data;

public class Chat_Data {
    private String Uid;
    private String Text;
    private String WriteDate;
    private String Reply_Target_Text;
    private String Reply_Target_User_Uid;
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

    public Chat_Data(){

    }

    public Chat_Data(String uid, String text, String writeDate, String reply_Target_Text, String reply_Target_User_Uid, String chat_ID) {
        this.Uid = uid;
        this.Text = text;
        this.WriteDate = writeDate;
        this.Reply_Target_Text = reply_Target_Text;
        this.Reply_Target_User_Uid = reply_Target_User_Uid;
        this.Chat_ID = chat_ID;
    }
}
