package com.example.team_practice;

public class chatItem {

    String name;
    String message;
    String time;
    String profileImgUrl;

    public chatItem() {

    }

    public chatItem(String name, String message, String time, String profileImgUrl) {
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

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }
}
