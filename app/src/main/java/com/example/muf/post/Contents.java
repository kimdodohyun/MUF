package com.example.muf.post;

public class Contents {
    private String profile_pic;
    private String Username;
    private String album_image;

    public Contents(){}

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getAlbum_image() {
        return album_image;
    }

    public void setAlbum_image(String album_image) {
        this.album_image = album_image;
    }

    public String getProfile_pic(){
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic){
        this.profile_pic = profile_pic;
    }
}
