package com.example.muf.post;

import com.google.firebase.Timestamp;

//사용자프로필사진, 사용자이름, 앨범title, 앨범img, inputtext를 넘겨야함
public class PostFireBase {
    private String profileimg;
    private String username;
    private String albumtitle;
    private String artis;
    private String albumimg;
    private String inputtext;
    private Timestamp timestamp;
    private String uid;
    public  PostFireBase(){}

    public PostFireBase(String Profileimg, String Username, String Albumtitle, String Artist,
                        String Albumimg, String Inputtext, Timestamp Timestamp, String Uid){
        this.profileimg = Profileimg;
        this.username = Username;
        this.albumtitle = Albumtitle;
        this.artis = Artist;
        this.albumimg = Albumimg;
        this.inputtext = Inputtext;
        this.timestamp = Timestamp;
        this.uid = Uid;
    }

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

    public String getArtis() { return artis; }
    public void setArtis(String artis) { this.artis = artis; }
}
