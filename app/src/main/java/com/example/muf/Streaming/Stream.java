package com.example.muf.Streaming;

public class Stream {
    private String trackName;
    private String ArtistName;
    private String trackImgUri;
    private String trackUri;

    public Stream(String trackName, String artistName, String trackImgUri, String trackUri) {
        this.trackName = trackName;
        ArtistName = artistName;
        this.trackImgUri = trackImgUri;
        this.trackUri = trackUri;
    }

    public String getTrackUri() {
        return trackUri;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return ArtistName;
    }

    public String getTrackImgUri() {
        return trackImgUri;
    }
}
