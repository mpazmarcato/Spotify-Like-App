package model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
public class Album extends Content {
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private final List<Song> songs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private User artist;

    public Album() {}

    public Album(int id, String title, int duration, String description, User artist) {
        super(id, title, duration, description);
        this.artist = artist;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public User getArtist() {
        return artist;
    }

    public void setArtist(User artist) {
        this.artist = artist;
    }
}
