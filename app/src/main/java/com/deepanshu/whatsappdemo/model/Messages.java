package com.deepanshu.whatsappdemo.model;

public class Messages {
    private String from,to,messageId,date,time;
    private String message;
    private String type;

    public Messages(){

    }


    public String getFrom() {
        return from;
    }

    public Messages(String from, String to, String messageId, String date, String time, String message, String type) {
        this.from = from;
        this.to = to;
        this.messageId = messageId;
        this.date = date;
        this.time = time;
        this.message = message;
        this.type = type;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
