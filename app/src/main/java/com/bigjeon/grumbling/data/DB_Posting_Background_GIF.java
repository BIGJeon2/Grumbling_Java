package com.bigjeon.grumbling.data;

public class DB_Posting_Background_GIF {
    private String  GIF;
    private String GIF_Name;

    public DB_Posting_Background_GIF(String GIF, String GIF_Name) {
        this.GIF = GIF;
        this.GIF_Name = GIF_Name;
    }

    public DB_Posting_Background_GIF() {
    }

    public String getGIF() {
        return GIF;
    }

    public void setGIF(String GIF) {
        this.GIF = GIF;
    }

    public String getGIF_Name() {
        return GIF_Name;
    }

    public void setGIF_Name(String GIF_Name) {
        this.GIF_Name = GIF_Name;
    }
}
