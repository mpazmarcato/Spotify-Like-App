package services;

import model.Playlist;
import repositories.PlaylistRepository;

import java.util.List;
import java.util.Optional;

public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Optional<Playlist> savePlaylist(Playlist playlist) {
        return playlistRepository.savePlaylist(playlist);
    }

    public Optional<Playlist> findPlaylistById(int id) {
        return playlistRepository.findPlaylistById(id);
    }

    public List<Playlist> findAllPlaylists() {
        return playlistRepository.findAllPlaylists();
    }

    public Optional<Playlist> updatePlaylist(Playlist playlist) {
        return playlistRepository.updatePlaylist(playlist);
    }

    public void deletePlaylist(int id) {
         playlistRepository.deletePlaylistById(id);
    }
}
