package com.bigjeon.grumbling.Model;

public class NotificationModel {
    private String title;
    private String body;
    private String tag;
    private String data;

    public NotificationModel(String title, String body, String tag, String data) {
        this.title = title;
        this.body = body;
        this.tag = tag;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
