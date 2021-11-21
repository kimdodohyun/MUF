package com.example.muf.model;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private String nickName;
    private String profileImageUrl;
    private String profileMusicUrl;
    private String uid;
    private ArrayList<String> myzonelist;
    private int postcount;
    private int songcount;

    public UserModel(){}
    public UserModel(String nickName, String profileImageUrl, String profileMusicUrl,
                     String uid, ArrayList<String> Myzonelist, int Postcount, int Songcount) {
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
        this.profileMusicUrl = profileMusicUrl;
        this.uid = uid;
        this.myzonelist = Myzonelist;
        this.postcount = Postcount;
        this.songcount = Songcount;
    }

    public int getSongcount() { return songcount; }
    public void setSongcount(int songcount) { this.songcount = songcount; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public int getPostcount() { return postcount; }
    public void setPostcount(int postcount) { this.postcount = postcount; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getProfileMusicUrl() { return profileMusicUrl; }
    public void setProfileMusicUrl(String profileMusicUrl) { this.profileMusicUrl = profileMusicUrl; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public ArrayList<String> getMyzonelist() { return myzonelist; }
    public void setMyzonelist(ArrayList<String> myzonelist) { this.myzonelist = myzonelist; }
}
