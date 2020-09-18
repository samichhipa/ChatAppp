package com.example.chatapp.Model;

public class GroupMembers {

    String designation,timestamp,user_id;

    public GroupMembers() {
    }

    public GroupMembers(String designation, String timestamp, String user_id) {
        this.designation = designation;
        this.timestamp = timestamp;
        this.user_id = user_id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
