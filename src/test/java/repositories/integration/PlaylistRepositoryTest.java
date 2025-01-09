package repositories.integration;

import model.Playlist;
import org.junit.jupiter.api.*;
import repositories.PlaylistRepository;
import util.HibernateUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistRepositoryTest {

    private static PlaylistRepository playlistRepository;
    private Playlist testPlaylist;

    @BeforeAll
    static void setUpClass() {
        playlistRepository = new PlaylistRepository();
    }

    @BeforeEach
    void setUp() {
        testPlaylist = new Playlist();
        testPlaylist.setTitle("testPlaylist");
    }

    @AfterEach
    void cleanUp() {
        try {
            if (testPlaylist != null && testPlaylist.getId() != null) {
                playlistRepository.deletePlaylistById(testPlaylist.getId());
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
    void testSavePlaylist() {
        Optional<Playlist> savedPlaylist = playlistRepository.savePlaylist(testPlaylist);
        assertTrue(savedPlaylist.isPresent());
        assertEquals("testPlaylist", savedPlaylist.get().getTitle());
        testPlaylist = savedPlaylist.get();
    }

    @Test
    void testFindPlaylistById() {
        Optional<Playlist> savedPlaylist = playlistRepository.savePlaylist(testPlaylist);
        assertTrue(savedPlaylist.isPresent());
        testPlaylist = savedPlaylist.get();

        Optional<Playlist> foundPlaylist = playlistRepository.findPlaylistById(testPlaylist.getId());
        assertTrue(foundPlaylist.isPresent());
        assertEquals("testPlaylist", foundPlaylist.get().getTitle());
    }

    @Test
    void testUpdatePlaylist() {
        Optional<Playlist> savedPlaylist = playlistRepository.savePlaylist(testPlaylist);
        assertTrue(savedPlaylist.isPresent());
        testPlaylist = savedPlaylist.get();

        testPlaylist.setTitle("newPlaylist");
        Optional<Playlist> updatedPlaylist = playlistRepository.updatePlaylist(testPlaylist);
        assertTrue(updatedPlaylist.isPresent());
        assertEquals("newPlaylist", updatedPlaylist.get().getTitle());
    }

    @Test
    void testDeletePlaylistById() {
        Optional<Playlist> savedPlaylist = playlistRepository.savePlaylist(testPlaylist);
        assertTrue(savedPlaylist.isPresent());
        testPlaylist = savedPlaylist.get();

        Integer playlistId = testPlaylist.getId();
        playlistRepository.deletePlaylistById(playlistId);

        Optional<Playlist> deletedPlaylist = playlistRepository.findPlaylistById(playlistId);
        assertFalse(deletedPlaylist.isPresent());
        testPlaylist = null;
    }

    @Test
    void testSavePlaylistFails() {
        Optional<Playlist> result = playlistRepository.savePlaylist(null);
        assertFalse(result.isPresent(), "Expected savePlaylist to fail and return Optional.empty");
    }

    @Test
    void testFindPlaylistByIdFails() {
        Optional<Playlist> playlist = playlistRepository.findPlaylistById(-1);
        assertFalse(playlist.isPresent(), "Expected findPlaylistById to return Optional.empty for non-existent ID");
    }

    @Test
    void testUpdatePlaylistFails() {
        Playlist invalidPlaylist = new Playlist();
        invalidPlaylist.setId(-1);
        Optional<Playlist> result = playlistRepository.updatePlaylist(invalidPlaylist);
        assertFalse(result.isPresent(), "Expected updatePlaylist to fail and return Optional.empty for non-existent playlist");
    }

    @Test
    void testDeletePlaylistByIdFails() {

        assertDoesNotThrow(() -> playlistRepository.deletePlaylistById(-1),
                "Expected deletePlaylistById to handle non-existent ID gracefully");
    }
}
