package com.example.muf.post;

//사용자프로필사진, 사용자이름, 앨범title, 앨범img, inputtext를 넘겨야함
public class PostFireBase {
    private String profileimg;
    private String username;
    private String albumtitle;
    private String albumimg;
    private String inputtext;

    public PostFireBase(String Profileimg, String Username, String Albumtitle, String Albumimg, String Inputtext){
        this.profileimg = Profileimg;
        this.username = Username;
        this.albumtitle = Albumtitle;
        this.albumimg = Albumimg;
        this.inputtext = Inputtext;
    }

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
}
