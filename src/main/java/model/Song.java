package model;

import enums.SongType;

import jakarta.persistence.*;
import java.io.File;
import java.util.Date;

@Entity
@Table(name = "song")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String genre;
    private String artist;
    private String album;
    private Integer duration;
    @Temporal(TemporalType.DATE)
    private Date releaseYear;
    @Enumerated(EnumType.STRING)
    private SongType type;
    private String fileName;  // Nome do arquivo da música

    public Song() {}

    public Song(Integer id, String title, String genre, String artist, String album, Integer duration, Date releaseYear,
                SongType type, String fileName) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.type = type;
        this.fileName = fileName;  // Atribuindo o nome do arquivo da música
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Retorna o caminho completo da música.
     *
     * @return Caminho completo da música dentro da pasta de mídia.
     */
    public String getPath() {
        String mediaFolderPath = "src/java/main/media";  // Caminho da pasta de mídia (ajuste conforme necessário)
        return mediaFolderPath + File.separator + genre + File.separator + fileName;
    }
}
