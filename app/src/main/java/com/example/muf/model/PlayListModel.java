package com.example.muf.model;

import java.util.ArrayList;

public class PlayListModel {
    private String uid;
    private ArrayList<String> playlist;

    public PlayListModel(String uid, ArrayList<String> playlist) {
        this.uid = uid;
        this.playlist = playlist;
    }

    public String getUid() {
        return uid;
    }

    public ArrayList<String> getPlaylist() {
        return playlist;
    }
}

