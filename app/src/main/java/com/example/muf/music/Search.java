package com.example.muf.music;

import com.example.muf.model.Music;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class Search {
    public interface View {
        void reset();

        void addData(List<Track> items);
    }

    public interface ActionListener {

        void init(String token);

        String getCurrentQuery();

        void search(String searchQuery);

        void loadMoreResults();

        Music selectTrack(Track item);

        void resume();

        void pause();

        void destroy();

    }
}
