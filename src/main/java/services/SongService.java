package services;

import model.Song;
import repositories.SongRepository;

import java.util.List;
import java.util.Optional;

public class SongService {
    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public Optional<Song> saveSong(Song song) {
        return songRepository.saveSong(song);
    }

    public Optional<Song> findSongById(int id) {
        return songRepository.findSongById(id);
    }

    public Optional<Song> updateSong(Song song) {
        return songRepository.updateSong(song);
    }

    public void deleteSongById(int id) {
        songRepository.deleteSongById(id);
    }

    public List<Song> findAllSongs() {
        return songRepository.findAllSongs();
    }

    public List<Song> findAllPodcastsByTitle(String title) {
        return songRepository.findAllPodcastsByTitle(title);
    }

}
