package controller;

import model.Album;
import services.AlbumService;

import java.util.List;
import java.util.Optional;

public class AlbumController {
    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    public Optional<Album> saveAlbum(Album album) {
        return albumService.saveAlbum(album);
    }

    public Optional<Album> findAlbumById(int id) {
        return albumService.findAlbumById(id);
    }

    public Optional<Album> updateAlbum(Album album) {
        return albumService.updateAlbum(album);
    }

    public void deleteAlbumById(int id) {
        albumService.deleteAlbumById(id);
    }

    public List<Album> findAllAlbums() {
        return albumService.findAllAlbums();
    }
}
