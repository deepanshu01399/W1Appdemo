package com.deepanshu.whatsappdemo.model;

public class NotificationData {
    private String data,user,title,sented;
    private int icons;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public int getIcons() {
        return icons;
    }

    public void setIcons(int icons) {
        this.icons = icons;
    }

    public NotificationData(String data, String user, String title, String sented, int icons) {
        this.data = data;
        this.user = user;
        this.title = title;
        this.sented = sented;
        this.icons = icons;
    }
}
