package com.example.muf.music;

import java.io.Serializable;

public class Music implements Serializable {
    private String id;
    private String uri;
    private String title;
    private String album_name;
    private String img_uri;
    private String artist_name;
    private String artist_id;

    public Music(String id, String uri, String title, String album_name, String img_uri, String artist_name, String artist_id) {
        this.id = id;
        this.uri = uri;
        this.title = title;
        this.album_name = album_name;
        this.img_uri = img_uri;
        this.artist_name = artist_name;
        this.artist_id = artist_id;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getArtist_id() {
        return artist_id;
    }

}
