package repositories.integration;

import model.Album;
import org.junit.jupiter.api.*;
import repositories.AlbumRepository;
import util.HibernateUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AlbumRepositoryTest {

    private static AlbumRepository albumRepository;
    private Album testAlbum;

    @BeforeAll
    static void setUpClass() {
        albumRepository = new AlbumRepository();
    }

    @BeforeEach
    void setUp() {
        testAlbum = new Album();
        testAlbum.setTitle("testAlbum");
    }

    @AfterEach
    void cleanUp() {
        try {
            if (testAlbum != null && testAlbum.getId() != null) {
                albumRepository.deleteAlbumById(testAlbum.getId());
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    void testSaveAlbum() {
        Optional<Album> savedAlbum = albumRepository.saveAlbum(testAlbum);
        assertTrue(savedAlbum.isPresent());
        assertEquals("testAlbum", savedAlbum.get().getTitle());
        testAlbum = savedAlbum.get();
    }

    @Test
    void testFindAlbumById() {
        Optional<Album> savedAlbum = albumRepository.saveAlbum(testAlbum);
        assertTrue(savedAlbum.isPresent());
        testAlbum = savedAlbum.get();

        Optional<Album> foundAlbum = albumRepository.findAlbumById(testAlbum.getId());
        assertTrue(foundAlbum.isPresent());
        assertEquals("testAlbum", foundAlbum.get().getTitle());
    }

    @Test
    void testUpdateAlbum() {
        Optional<Album> savedAlbum = albumRepository.saveAlbum(testAlbum);
        assertTrue(savedAlbum.isPresent());
        testAlbum = savedAlbum.get();

        testAlbum.setTitle("newAlbum");
        Optional<Album> updatedAlbum = albumRepository.updateAlbum(testAlbum);
        assertTrue(updatedAlbum.isPresent());
        assertEquals("newAlbum", updatedAlbum.get().getTitle());
    }

    @Test
    void testDeleteAlbumById() {
        Optional<Album> savedAlbum = albumRepository.saveAlbum(testAlbum);
        assertTrue(savedAlbum.isPresent());
        testAlbum = savedAlbum.get();

        Integer albumId = testAlbum.getId();
        albumRepository.deleteAlbumById(albumId);

        Optional<Album> deletedAlbum = albumRepository.findAlbumById(albumId);
        assertFalse(deletedAlbum.isPresent());
        testAlbum = null;
    }

    @Test
    void testSaveAlbumFails() {
        Optional<Album> result = albumRepository.saveAlbum(null);
        assertFalse(result.isPresent(), "Expected saveAlbum to fail and return Optional.empty");
    }

    @Test
    void testFindAlbumByIdFails() {
        Optional<Album> album = albumRepository.findAlbumById(-1);
        assertFalse(album.isPresent(), "Expected findAlbumById to return Optional.empty for non-existent ID");
    }

    @Test
    void testUpdateAlbumFails() {
        Album invalidAlbum = new Album();
        invalidAlbum.setId(-1);
        Optional<Album> result = albumRepository.updateAlbum(invalidAlbum);
        assertFalse(result.isPresent(), "Expected updateAlbum to fail and return Optional.empty for non-existent album");
    }

    @Test
    void testDeleteAlbumByIdFails() {
        assertDoesNotThrow(() -> albumRepository.deleteAlbumById(-1),
                "Expected deleteAlbumById to handle non-existent ID gracefully");
    }
}
