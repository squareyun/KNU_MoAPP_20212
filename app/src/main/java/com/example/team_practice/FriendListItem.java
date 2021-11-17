package com.example.team_practice;

public class FriendListItem {
    int resId;
    String name;
    String walkCnt;

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWalkCnt() {
        return walkCnt;
    }

    public void setWalkCnt(String walkCnt) {
        this.walkCnt = walkCnt;
    }

    public FriendListItem(int resId, String name, String walkCnt) {
        this.resId = resId;
        this.name = name;
        this.walkCnt = walkCnt;
    }
}
