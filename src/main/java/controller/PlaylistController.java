package controller;

import model.Playlist;
import services.PlaylistService;

import java.util.List;
import java.util.Optional;

public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public Optional<Playlist> savePlaylist(Playlist playlist) {
        return playlistService.savePlaylist(playlist);
    }

    public Optional<Playlist> findPlaylistById(Integer id) {
        return playlistService.findPlaylistById(id);
    }

    public Optional<Playlist> updatePlaylist(Playlist playlist) {
        return playlistService.updatePlaylist(playlist);
    }

    public void deletePlaylist(Integer id) {
        playlistService.deletePlaylist(id);
    }

    public List<Playlist> findAllPlaylists() {
        return playlistService.findAllPlaylists();
    }
}
