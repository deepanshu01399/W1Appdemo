package com.deepanshu.whatsappdemo;

import java.util.Date;

public class GroupMessages {

    private String  date;
    private String message;
    private String time;

    public GroupMessages() {
    }

    private String name;
    //private String messageType;

    public GroupMessages(String date, String message, String time, String name, String fromUser) {
        this.date = date;
        this.message = message;
        this.time = time;
        this.name = name;
        //this.messageType = messageType;
        //this.fromUser = fromUser;
    }

    /*public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }*/



   /* public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
*/
    private String fromUser;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }







}
