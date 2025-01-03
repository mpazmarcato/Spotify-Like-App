package model;

import java.util.List;

public class Playlist extends Content {
    private List<Song> songs;

    public Playlist(int id, String title, List<String> contributors, int duration, String description, List<Song> songs) {
        super(id, title, contributors, duration, description);
        this.songs = songs;
    }

    public List<Song> getSongs() {
        return songs;
    }

}
