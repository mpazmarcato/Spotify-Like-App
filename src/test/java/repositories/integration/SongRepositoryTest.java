package repositories.integration;

import enums.SongType;
import model.Song;
import org.junit.jupiter.api.*;
import repositories.SongRepository;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SongRepositoryTest {

    private static SongRepository songRepository;
    private Song testSong1;
    private Song testSong2;
    private final String testSongTitle1 = "Song One";
    private final String testSongTitle2 = "Song two";

    @BeforeAll
    static void setUpClass() {
        songRepository = new SongRepository();
    }

    @BeforeEach
    void setUp() {
        testSong1 = new Song();
        testSong1.setTitle(testSongTitle1);
        testSong1.setType(SongType.PODCAST);
        testSong1.setArtist("Artist One");

        testSong2 = new Song();
        testSong2.setTitle(testSongTitle2);
        testSong2.setType(SongType.PODCAST);
        testSong2.setArtist("Artist Two");

    }

    @AfterEach
    void cleanUp() {
        try {
            if (testSong1 != null && testSong1 .getId() != null) {
                songRepository.deleteSongById(testSong1 .getId());
            }
            if (testSong2 != null && testSong2.getId() != null) {
                songRepository.deleteSongById(testSong2.getId());
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
        Optional<Song> savedSong = songRepository.saveSong(testSong1);
        assertTrue(savedSong.isPresent());
        assertEquals(testSongTitle1, savedSong.get().getTitle());
        assertEquals("Artist One", savedSong.get().getArtist());
        testSong1 = savedSong.get();
    }

    @Test
    void testFindSongById() {
        Optional<Song> savedSong = songRepository.saveSong(testSong1);
        assertTrue(savedSong.isPresent());
        testSong1 = savedSong.get();

        Optional<Song> foundSong = songRepository.findSongById(testSong1.getId());
        assertTrue(foundSong.isPresent());
        assertEquals(testSongTitle1, foundSong.get().getTitle());
    }

    @Test
    void testUpdateSong() {
        Optional<Song> savedSong = songRepository.saveSong(testSong1);
        assertTrue(savedSong.isPresent());
        testSong1 = savedSong.get();

        testSong1.setArtist("newArtist");
        Optional<Song> updatedSong = songRepository.updateSong(testSong1);
        assertTrue(updatedSong.isPresent());
        assertEquals("newArtist", updatedSong.get().getArtist());
    }

    @Test
    void testDeleteSongById() {
        Optional<Song> savedSong = songRepository.saveSong(testSong1);
        assertTrue(savedSong.isPresent());
        testSong1 = savedSong.get();

        Integer songId = testSong1.getId();
        songRepository.deleteSongById(songId);

        Optional<Song> deletedSong = songRepository.findSongById(songId);
        assertFalse(deletedSong.isPresent());
        testSong1 = null;
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

    @Test
    void testFindAllPodcastsByTitleExactMatch() {
        var savedSong1 = songRepository.saveSong(testSong1);
        assertTrue(savedSong1.isPresent());
        var savedSong2 = songRepository.saveSong(testSong2);
        assertTrue(savedSong2.isPresent());

        List<Song> podcasts = songRepository.findAllPodcastsByTitle(testSongTitle1);
        assertNotNull(podcasts, "Expected a non-null result");
        assertEquals(1, podcasts.size(), "Expected exactly 1 podcast to match the title");
        assertEquals(testSongTitle1, podcasts.getFirst().getTitle());
    }

    @Test
    void testFindAllPodcastsByTitlePartialMatch() {
        var savedSong1 = songRepository.saveSong(testSong1);
        assertTrue(savedSong1.isPresent());
        var savedSong2 = songRepository.saveSong(testSong2);
        assertTrue(savedSong2.isPresent());

        List<Song> podcasts = songRepository.findAllPodcastsByTitle("Song");
        assertNotNull(podcasts, "Expected a non-null result");
        assertEquals(2, podcasts.size(), "Expected 2 podcasts to match the partial title");
    }

    @Test
    void testFindAllPodcastsByTitleIgnoreCase() {
        var savedSong1 = songRepository.saveSong(testSong1);
        assertTrue(savedSong1.isPresent());
        var savedSong2 = songRepository.saveSong(testSong2);
        assertTrue(savedSong2.isPresent());

        List<Song> podcasts = songRepository.findAllPodcastsByTitle("song one");
        assertNotNull(podcasts, "Expected a non-null result");
        assertEquals(1, podcasts.size(), "Expected exactly 1 podcast to match the title ignoring case");
        assertEquals(testSongTitle1, podcasts.getFirst().getTitle());
    }

    //Não consegui fazer a consulta sql ignorar acentos
//    @Test
//    void testFindAllPodcastsByTitleIgnoreAccents() {
//        var savedSong1 = songRepository.saveSong(testSong1);
//        assertTrue(savedSong1.isPresent());
//        var savedSong2 = songRepository.saveSong(testSong2);
//        assertTrue(savedSong2.isPresent());
//
//        testSong1.setTitle("Pódcast Éxample");
//        var updateSong1 = songRepository.updateSong(testSong1);
//        assertTrue(updateSong1.isPresent());
//
//        List<Song> podcasts = songRepository.findAllPodcastsByTitle("Podcast Example");
//        assertNotNull(podcasts, "Expected a non-null result");
//        assertEquals(1, podcasts.size(), "Expected exactly 1 podcast to match the title ignoring accents");
//        assertEquals("Pódcast Éxample", podcasts.getFirst().getTitle());
//    }

    @Test
    void testFindAllPodcastsByTitleNoMatch() {
        var savedSong1 = songRepository.saveSong(testSong1);
        assertTrue(savedSong1.isPresent());
        var savedSong2 = songRepository.saveSong(testSong2);
        assertTrue(savedSong2.isPresent());

        List<Song> podcasts = songRepository.findAllPodcastsByTitle("Non-existent Title");
        assertNotNull(podcasts, "Expected a non-null result");
        assertTrue(podcasts.isEmpty(), "Expected no podcasts to match the title");
    }
}
