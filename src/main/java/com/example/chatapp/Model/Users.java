package com.example.chatapp.Model;

public class Users {

    String email,username,image,id,password,online_status,typing_to;


    public Users() {
    }

    public Users(String email, String username, String image,String id,String pass,String stat,String to) {
        this.email = email;
        this.username = username;
        this.image = image;
        this.id=id;
        this.password=pass;
        online_status=stat;
        typing_to=to;

    }

    public String getTyping_to() {
        return typing_to;
    }

    public void setTyping_to(String typing_to) {
        this.typing_to = typing_to;
    }

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
