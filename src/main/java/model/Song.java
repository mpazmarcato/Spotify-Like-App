package model;

import java.util.Date;

import enums.SongType;

import jakarta.persistence.*;

@Entity
@Table(name = "song")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String genre;
    private String artist;
    private String album;
    private int duration;
    @Temporal(TemporalType.DATE)
    private Date releaseYear;
    @Enumerated(EnumType.STRING)
    private SongType type;

    public Song() {}

    public Song(int id, String title, String genre, String artist, String album, int duration, Date releaseYear,
            SongType type) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Date releaseYear) {
        this.releaseYear = releaseYear;
    }

    public SongType getType() {
        return type;
    }

    public void setType(SongType type) {
        this.type = type;
    }

    public String getFilePath(Song song) {
        String basePath = "src/main/resources/";
        String genre = song.getGenre();
        String artist = song.getArtist();

        genre = genre.replaceAll("[^a-zA-Z0-9]", "");
        artist = artist.replaceAll("[^a-zA-Z0-9]", "");

        return basePath + "/" + genre + "/" + artist;
    }
}
