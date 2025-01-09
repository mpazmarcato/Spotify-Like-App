package repositories;

import model.Album;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class AlbumRepository {

    private static final Logger logger = LoggerFactory.getLogger(AlbumRepository.class);

    public Optional<Album> saveAlbum(Album album) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            session.persist(album);
            transaction.commit();
            return Optional.of(album);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Error during transaction rollback", rollbackEx);
                }
            }
            logger.error("Error saving album", e);
            return Optional.empty();
        }
    }

    public Optional<Album> findAlbumById(int id) {
        try (Session session = HibernateUtil.getSession()) {
            Album album = session.get(Album.class, id);
            return Optional.ofNullable(album);
        } catch (Exception e) {
            logger.error("Error getting album by ID", e);
            return Optional.empty();
        }
    }

    public List<Album> findAllAlbums() {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery("SELECT u FROM model.Album u", Album.class).list();
        } catch (Exception e) {
            logger.error("Error getting all albums", e);
            return null;
        }
    }

    public Optional<Album> updateAlbum(Album album) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            Album existingAlbum = session.get(Album.class, album.getId());
            if (existingAlbum == null) {
                return Optional.empty();
            }

            transaction = session.beginTransaction();
            session.merge(existingAlbum);
            transaction.commit();
            return Optional.of(album);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Error during transaction rollback", rollbackEx);
                }
            }
            logger.error("Error saving album", e);
            return Optional.empty();
        }
    }

    public void deleteAlbumById(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            Album album = session.get(Album.class, id);
            if (album != null) {
                session.remove(album);
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
            logger.error("Error saving album", e);
        }
    }
}
