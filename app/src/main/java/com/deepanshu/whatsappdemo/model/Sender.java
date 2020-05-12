package com.deepanshu.whatsappdemo.model;

public class Sender {
    private String to;
    private NotificationData notificationData;

    public Sender() {
    }

    public Sender(String to, NotificationData notificationData) {
        this.to = to;
        this.notificationData = notificationData;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

}
