package com.example.muf.model;

import java.util.List;

public class UserModel {
    private String profileImageUrl;
    private String nickName;
    private String profileMusicUrl;
    private String uid;

    public UserModel(){}

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public String getProfileMusicUrl() { return profileMusicUrl; }
    public void setProfileMusicUrl(String profileMusicUrl) { this.profileMusicUrl = profileMusicUrl; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
}
