package controller;

import model.Song;
import services.SongService;

import java.util.List;
import java.util.Optional;

public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    public Optional<Song> saveSong(Song Song) {
        return songService.saveSong(Song);
    }

    public Optional<Song> findSongById(int id) {
        return songService.findSongById(id);
    }

    public List<Song> findAllSongs() {
        return songService.findAllSongs();
    }

    public Optional<Song> updateSong(Song Song) {
        return songService.updateSong(Song);
    }

    public void deleteSongById(int id) {
        songService.deleteSongById(id);
    }

    public List<Song> findAllPodcastsByTitle(String title) {
        return songService.findAllPodcastsByTitle(title);
    }
}
