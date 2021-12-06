package com.example.team_practice;

public class FriendListItem {
    String ID;
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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public FriendListItem(String ID, String name, String walkCnt) {
        this.ID = ID;
        this.name = name;
        this.walkCnt = walkCnt;
    }

}
