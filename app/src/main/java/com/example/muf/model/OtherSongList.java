package com.example.muf.model;

import java.util.HashMap;
import java.util.Map;

public class OtherSongList {
    private String uid;
    private HashMap<String, Integer> map;

    public OtherSongList(){
        this.uid = "";
        this.map = new HashMap<>();
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public HashMap<String, Integer> getMap() { return map; }
    public void setMap(HashMap<String, Integer> map) { this.map = map; }

    public void putData(String artistName){
        this.map.put(artistName, 1);
    }
}
