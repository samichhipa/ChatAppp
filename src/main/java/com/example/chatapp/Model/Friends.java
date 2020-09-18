package com.example.chatapp.Model;

public class Friends {

    String friend_id;
    String date;

    public Friends() {
    }

    public Friends(String friend_id, String date) {
        this.friend_id = friend_id;
        this.date = date;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
