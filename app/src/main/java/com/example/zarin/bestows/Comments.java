package com.example.zarin.bestows;

import java.util.Date;

public class Comments {

    private String message, user_id,name;
    private Date timestamp;

    public Comments(){

    }

    public Comments(String message, String name, Date timestamp,String user_id) {
        this.message = message;
        this.name = name;
        this.timestamp = timestamp;
        this.user_id=user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

