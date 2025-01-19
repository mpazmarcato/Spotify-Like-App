package repositories;

import model.Playlist;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class PlaylistRepository {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistRepository.class);

    public Optional<Playlist> savePlaylist(Playlist playlist) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            session.persist(playlist);
            transaction.commit();
            return Optional.of(playlist);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Error during transaction rollback", rollbackEx);
                }
            }
            logger.error("Error saving playlist", e);
            return Optional.empty();
        }
    }

    public Optional<Playlist> findPlaylistById(int id) {
        try (Session session = HibernateUtil.getSession()) {
            Playlist playlist = session.get(Playlist.class, id);
            return Optional.ofNullable(playlist);
        } catch (Exception e) {
            logger.error("Error getting playlist by ID", e);
            return Optional.empty();
        }
    }

    public List<Playlist> findAllPlaylists() {
        try (Session session = HibernateUtil.getSession()) {
            String hql = "SELECT p FROM model.Playlist p LEFT JOIN FETCH p.songs";
            return session.createQuery(hql, Playlist.class).list();
        } catch (Exception e) {
            logger.error("Error getting all playlists", e);
            return null;
        }
    }

    public Optional<Playlist> updatePlaylist(Playlist playlist) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            Playlist existingPlaylist = session.get(Playlist.class, playlist.getId());
            if (existingPlaylist == null) {
                return Optional.empty();
            }

            transaction = session.beginTransaction();
            session.merge(existingPlaylist);
            transaction.commit();
            return Optional.of(playlist);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Error during transaction rollback", rollbackEx);
                }
            }
            logger.error("Error saving playlist", e);
            return Optional.empty();
        }
    }

    public void deletePlaylistById(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            Playlist playlist = session.get(Playlist.class, id);
            if (playlist != null) {
                session.remove(playlist);
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
            logger.error("Error saving playlist", e);
        }
    }
}
