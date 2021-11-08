package com.example.muf.model;

import java.util.List;

public class UserModel {
    private String nickName;
    private String profileImageUrl;
    private String profileMusicUrl;
    private String uid;

    public UserModel(){}
    public UserModel(String nickName, String profileImageUrl, String profileMusicUrl, String uid) {
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
        this.profileMusicUrl = profileMusicUrl;
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileMusicUrl() {
        return profileMusicUrl;
    }

    public String getUid() {
        return uid;
    }
}

