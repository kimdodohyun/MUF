package com.example.muf.post;

public class Contents {
    private String profile_pic;
    private String Username;
    private String albumtitle;
    private String artistname;
    private String album_image;
    private String text;

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

    public String getAlbumtitle() { return albumtitle; }
    public void setAlbumtitle(String albumtitle) { this.albumtitle = albumtitle; }

    public String getArtistname() { return artistname; }
    public void setArtistname(String artistname) { this.artistname = artistname; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
