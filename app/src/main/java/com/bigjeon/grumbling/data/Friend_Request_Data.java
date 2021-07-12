package com.bigjeon.grumbling.data;

public class Friend_Request_Data {
    private String State;
    private String Uid;
    private String Send_Date;

    public Friend_Request_Data(){

    }

    public Friend_Request_Data(String state, String uid, String send_Date) {
        State = state;
        Uid = uid;
        Send_Date = send_Date;
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

    public String getSend_Date() {
        return Send_Date;
    }

    public void setSend_Date(String send_Date) {
        Send_Date = send_Date;
    }
}
