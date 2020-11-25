package com.group05.mylocation.Modal;

import java.util.Calendar;
import java.util.Date;

public class Messages {
    String sender;
    String receiver;
    String message;

    public Messages(){}
    public Messages(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        Date currentTime = Calendar.getInstance().getTime();
        this.time = currentTime.toString();
        this.type = 1;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    int type;
    String time;
}
