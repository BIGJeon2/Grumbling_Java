package com.bigjeon.grumbling.data;

public class Friend_Data {
    private String Uid;
    private String Date;

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Friend_Data(){

    }

    public Friend_Data(String uid, String date) {
        Uid = uid;
        Date = date;
    }
}
