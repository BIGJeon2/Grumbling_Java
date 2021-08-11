package com.bigjeon.grumbling.Model;

public class Data {
    private String title;
    private String body;
    private String tag;
    private String click_action;
    private String data;
    private String img;

    public Data(String title, String body, String tag, String click_action, String data, String img) {
        this.title = title;
        this.body = body;
        this.tag = tag;
        this.click_action = click_action;
        this.data = data;
        this.img = img;
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

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
