package DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Playlist;
import model.User;

import java.util.List;

public class PlaylistDAO {

    private final EntityManager entityManager;

    public PlaylistDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Método para buscar playlists de um usuário específico
    public List<Playlist> findByUser(User user) {
        TypedQuery<Playlist> query = entityManager.createQuery(
                "SELECT p FROM Playlist p WHERE p.user = :user", Playlist.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    // Método para salvar uma nova playlist
    public void save(Playlist playlist) {
        entityManager.getTransaction().begin();
        entityManager.persist(playlist);
        entityManager.getTransaction().commit();
    }
}
