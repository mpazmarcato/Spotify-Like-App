package services;

import model.Album;
import repositories.AlbumRepository;

import java.util.List;
import java.util.Optional;

public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Optional<Album> saveAlbum(Album album) {
        return albumRepository.saveAlbum(album);
    }

    public Optional<Album> findAlbumById(int id) {
        return albumRepository.findAlbumById(id);
    }

    public Optional<Album> updateAlbum(Album album) {
        return albumRepository.updateAlbum(album);
    }

    public List<Album> findAllAlbums() {
        return albumRepository.findAllAlbums();
    }

    public void deleteAlbumById(int id) {
        albumRepository.deleteAlbumById(id);
    }
}
