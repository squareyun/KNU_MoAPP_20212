package com.example.team_practice;

public class ChatItem {

    String name;
    String message;
    String time;
    int profileImgUrl;

    public ChatItem() {
    }

    public ChatItem(String name, String message, String time, int profileImgUrl) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.profileImgUrl = profileImgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(int profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }
}
