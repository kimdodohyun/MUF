package com.example.muf.model;

import java.util.ArrayList;

public class FriendList {
    private String uid;
    private ArrayList<String> friendList;

    public FriendList(String uid, ArrayList<String> friendList) {
        this.uid = uid;
        this.friendList = friendList;
    }

    public String getUid() {
        return uid;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }
}
