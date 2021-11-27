package com.example.muf.communityfrag.post;

import com.google.firebase.Timestamp;

//사용자프로필사진, 사용자이름, 앨범title, 앨범img, inputtext를 넘겨야함
public class PostFireBase {
    private String profileimg;
    private String username;
    private String albumtitle;
    private String artist;
    private String albumimg;
    private String inputtext;
    private Timestamp timestamp;
    private String uid;
    private String uri;
    private int number;
    private String ename;

    public PostFireBase(){}

    public PostFireBase(String Profileimg, String Username, String Albumtitle, String Artist, String Albumimg,
                        String Inputtext, Timestamp Timestamp, String Uid, String Uri, int Number, String Ename){
        this.profileimg = Profileimg;
        this.username = Username;
        this.albumtitle = Albumtitle;
        this.artist = Artist;
        this.albumimg = Albumimg;
        this.inputtext = Inputtext;
        this.timestamp = Timestamp;
        this.uid = Uid;
        this.uri = Uri;
        this.number = Number;
        this.ename = Ename;
    }

    public String getEname() { return ename; }
    public void setEname(String ename) { this.ename = ename; }

    public String getUri() { return uri; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid;}

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getProfileimg() { return profileimg; }
    public void setProfileimg(String profileimg) { this.profileimg = profileimg; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAlbumtitle() { return albumtitle; }
    public void setAlbumtitle(String albumtitle) { this.albumtitle = albumtitle; }

    public String getAlbumimg() { return albumimg; }
    public void setAlbumimg(String albumimg) { this.albumimg = albumimg; }

    public String getInputtext() { return inputtext; }
    public void setInputtext(String inputtext) { this.inputtext = inputtext; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getArtis() { return artist; }
    public void setArtis(String artis) { this.artist = artis; }
}
