package com.example.muf.model;

import java.util.ArrayList;

public class UserModel {
    private String nickName;
    private String profileImageUrl;
    private String profileMusicUri;
    private String uid;
    private ArrayList<String> myzonelist;
    private int postcount;
    private int songcount;
    private int friendcount;

    public UserModel(UserModel userModel){
        this.nickName = userModel.getNickName();
        this.profileImageUrl = userModel.getProfileImageUrl();
        this.profileMusicUri = userModel.getProfileMusicUri();
        this.uid = userModel.getUid();
        this.myzonelist = userModel.getMyzonelist();
        this.postcount = userModel.getPostcount();
        this.songcount = userModel.getSongcount();
        this.friendcount = userModel.getFriendcount();
    }

    public UserModel(){}
    public UserModel(String nickName, String profileImageUrl, String profileMusicUrl,
                     String uid, ArrayList<String> Myzonelist, int Postcount, int Songcount, int Friendcount) {
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
        this.profileMusicUri = profileMusicUrl;
        this.uid = uid;
        this.myzonelist = Myzonelist;
        this.postcount = Postcount;
        this.songcount = Songcount;
        this.friendcount = Friendcount;
    }

    public int getFriendcount() { return friendcount; }
    public void setFriendcount(int friendcount) { this.friendcount = friendcount; }

    public int getSongcount() { return songcount; }
    public void setSongcount(int songcount) { this.songcount = songcount; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public int getPostcount() { return postcount; }
    public void setPostcount(int postcount) { this.postcount = postcount; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getProfileMusicUri() { return profileMusicUri; }
    public void setProfileMusicUri(String profileMusicUri) { this.profileMusicUri = profileMusicUri; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public ArrayList<String> getMyzonelist() { return myzonelist; }
    public void setMyzonelist(ArrayList<String> myzonelist) { this.myzonelist = myzonelist; }
}
