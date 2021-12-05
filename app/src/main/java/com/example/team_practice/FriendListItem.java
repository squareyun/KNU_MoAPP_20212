package com.example.team_practice;

public class FriendListItem {
    String name;
    String walkCnt;


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

    public FriendListItem(String name, String walkCnt) {
        this.name = name;
        this.walkCnt = walkCnt;
    }
}
