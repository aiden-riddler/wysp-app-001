package com.aidenriddler.wysp;

import java.util.Date;

public class ChatMessage {
    private String message;
    private String userID;
    private String email;
    private String profileImage;
    private Date timestamp;
    private Boolean sentByme;
    private Boolean received;
    private Boolean read;
    private Boolean delivered;

    public ChatMessage() {
    }

    public ChatMessage(String message, String userID, String email, String profileImage, Date timestamp, Boolean sentByme, Boolean received, Boolean read, Boolean delivered) {
        this.message = message;
        this.userID = userID;
        this.email = email;
        this.profileImage = profileImage;
        this.timestamp = timestamp;
        this.sentByme = sentByme;
        this.received = received;
        this.read = read;
        this.delivered = delivered;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSentByme() {
        return sentByme;
    }

    public void setSentByme(Boolean sentByme) {
        this.sentByme = sentByme;
    }

    public Boolean getReceived() {
        return received;
    }

    public void setReceived(Boolean received) {
        this.received = received;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }
}
