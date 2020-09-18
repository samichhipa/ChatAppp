package com.example.chatapp.Model;

public class Chat {

    String Sender,Receiver,Message,TimeStamp,ID,msg_type;
    boolean isSeen;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, String timeStamp, boolean isSeen,String id,String msg_typ) {
        Sender = sender;
        Receiver = receiver;
        Message = message;
        TimeStamp = timeStamp;
        this.isSeen = isSeen;
        ID=id;
        msg_type=msg_typ;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
