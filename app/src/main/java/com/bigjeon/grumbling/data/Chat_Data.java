package com.bigjeon.grumbling.data;

public class Chat_Data {
    private String Uid;
    private String Text;
    private String WriteDate;

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

    public Chat_Data(String uid, String text, String writeDate) {
        this.Uid = uid;
        this.Text = text;
        this.WriteDate = writeDate;
    }

    public Chat_Data(){

    }
}
