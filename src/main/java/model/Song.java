package model;

import java.io.File;
import java.util.Date;

public class Song {
    private int songID;
    private String title;
    private String genre;
    private String artist;
    private String album;
    private int duration;
    private Date releaseYear;

    public Song(int songID, String title, String genre, String artist, String album, int duration, Date releaseYear) {
        this.songID = songID;
        this.title = title;
        this.genre = genre;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.releaseYear = releaseYear;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
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

    public String getFilePath(Song song) {
        String basePath = "src/main/resources/";
        String genre = song.getGenre();
        String artist = song.getArtist();

        genre = genre.replaceAll("[^a-zA-Z0-9]", "");
        artist = artist.replaceAll("[^a-zA-Z0-9]", "");

        return basePath + "/" + genre + "/" + artist;
    }
}
