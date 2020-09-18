package com.example.chatapp.Model;

import com.example.chatapp.Common.Common;

public class GroupChat {

String sender,message,timestamp,msg_type,id;


    public GroupChat() {
    }

    public GroupChat(String sender, String message, String timestamp, String msg_type, String id) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.msg_type = msg_type;
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
