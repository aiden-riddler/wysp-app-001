package com.aidenriddler.wysp;

public class User {
    private String username;
    private String WySPID;

    public User() {
    }

    public User(String username, String wySPID) {
        this.username = username;
        WySPID = wySPID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWySPID() {
        return WySPID;
    }

    public void setWySPID(String wySPID) {
        WySPID = wySPID;
    }
}
