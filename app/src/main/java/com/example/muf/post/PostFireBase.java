package com.example.muf.post;

public class PostFireBase {
    private String contents;
    private String publisher;

    public PostFireBase(String Contents, String Publisher){
        this.contents = Contents;
        this.publisher = Publisher;
    }

    public String getContents(){return this.contents;}
    public void setContents(String contents) {this.contents = contents;}
    public String getPublisher(){return this.publisher;}
    public void setPublisher(String publisher) {this.publisher = publisher;}
}
