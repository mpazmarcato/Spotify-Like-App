package repositories.integration;

import model.Song;
import org.junit.jupiter.api.*;
import repositories.SongRepository;
import util.HibernateUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SongRepositoryTest {

    private static SongRepository songRepository;
    private Song testSong;

    @BeforeAll
    static void setUpClass() {
        songRepository = new SongRepository();
    }

    @BeforeEach
    void setUp() {
        testSong = new Song();
        testSong.setTitle("testsong");
        testSong.setArtist("artistTest");
    }

    @AfterEach
    void cleanUp() {
        try {
            if (testSong != null && testSong.getId() != null) {
                songRepository.deleteSongById(testSong.getId());
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
    void testSaveSong() {
        Optional<Song> savedSong = songRepository.saveSong(testSong);
        assertTrue(savedSong.isPresent());
        assertEquals("testsong", savedSong.get().getTitle());
        assertEquals("artistTest", savedSong.get().getArtist());
        testSong = savedSong.get();
    }

    @Test
    void testFindSongById() {
        Optional<Song> savedSong = songRepository.saveSong(testSong);
        assertTrue(savedSong.isPresent());
        testSong = savedSong.get();

        Optional<Song> foundSong = songRepository.findSongById(testSong.getId());
        assertTrue(foundSong.isPresent());
        assertEquals("testsong", foundSong.get().getTitle());
    }

    @Test
    void testUpdateSong() {
        Optional<Song> savedSong = songRepository.saveSong(testSong);
        assertTrue(savedSong.isPresent());
        testSong = savedSong.get();

        testSong.setArtist("newArtist");
        Optional<Song> updatedSong = songRepository.updateSong(testSong);
        assertTrue(updatedSong.isPresent());
        assertEquals("newArtist", updatedSong.get().getArtist());
    }

    @Test
    void testDeleteSongById() {
        Optional<Song> savedSong = songRepository.saveSong(testSong);
        assertTrue(savedSong.isPresent());
        testSong = savedSong.get();

        Integer songId = testSong.getId();
        songRepository.deleteSongById(songId);

        Optional<Song> deletedSong = songRepository.findSongById(songId);
        assertFalse(deletedSong.isPresent());
        testSong = null;
    }

    @Test
    void testSaveSongFails() {
        Optional<Song> result = songRepository.saveSong(null);
        assertFalse(result.isPresent(), "Expected saveSong to fail and return Optional.empty");
    }

    @Test
    void testFindSongByIdFails() {
        Optional<Song> song = songRepository.findSongById(-1);
        assertFalse(song.isPresent(), "Expected findSongById to return Optional.empty for non-existent ID");
    }

    @Test
    void testUpdateSongFails() {
        Song invalidSong = new Song();
        invalidSong.setId(-1);
        Optional<Song> result = songRepository.updateSong(invalidSong);
        assertFalse(result.isPresent(), "Expected updateSong to fail and return Optional.empty for non-existent song");
    }

    @Test
    void testDeleteSongByIdFails() {
        assertDoesNotThrow(() -> songRepository.deleteSongById(-1),
                "Expected deleteSongById to handle non-existent ID gracefully");
    }
}
