package repositories;

import model.Song;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class SongRepository {

    private static final Logger logger = LoggerFactory.getLogger(SongRepository.class);


    public Optional<Song> saveSong(Song song) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            session.persist(song);
            transaction.commit();
            return Optional.of(song);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Error during transaction rollback", rollbackEx);
                }
            }
            logger.error("Error saving song", e);
            return Optional.empty();
        }
    }

    public Optional<Song> findSongById(int id) {
        try (Session session = HibernateUtil.getSession()) {
            Song song = session.get(Song.class, id);
            return Optional.ofNullable(song);
        } catch (Exception e) {
            logger.error("Error getting song by ID", e);
            return Optional.empty();
        }
    }

    public List<Song> findAllSongs() {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery("SELECT u FROM model.Song u", Song.class).list();
        } catch (Exception e) {
            logger.error("Error getting all songs", e);
            return null;
        }
    }

    public Optional<Song> updateSong(Song song) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            Song existingSong = session.get(Song.class, song.getId());
            if (existingSong == null) {
                return Optional.empty();
            }

            transaction = session.beginTransaction();
            session.merge(existingSong);
            transaction.commit();
            return Optional.of(song);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Error during transaction rollback", rollbackEx);
                }
            }
            logger.error("Error update song", e);
            return Optional.empty();
        }
    }

    public void deleteSongById(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            Song song = session.get(Song.class, id);
            if (song != null) {
                session.remove(song);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Error during transaction rollback", rollbackEx);
                }
            }
            logger.error("Error delete song", e);
        }
    }
}
