package repositories;

import util.HibernateUtil;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public Optional<User> saveUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return Optional.of(user);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving user", e);
            return Optional.empty();
        }
    }

    public Optional<User> findUserById(int id) {
        try (Session session = HibernateUtil.getSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error getting user by ID", e);
            return Optional.empty();
        }
    }

    public List<User> findAllUsers() {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery("SELECT u FROM com.example.demo.model.User u", User.class).list();
        } catch (Exception e) {
            logger.error("Error getting all users", e);
            return null;
        }
    }

//    public Optional<User> findUserByUsername(String username) {
//        try (Session session = HibernateUtil.getSession()) {
//            return session.createQuery("SELECT u FROM com.example.demo.model.User u WHERE u.username = :username", User.class) //recebendo erro aqui
//                    .setParameter("username", username)
//                    .uniqueResultOptional();
//        } catch (Exception e) {
//            logger.error("Error getting user by username", e);
//            return Optional.empty();
//        }
//    }

    public Optional<User> updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            return Optional.of(user);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating user", e);
            return Optional.empty();
        }
    }

    public void deleteUserById(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id); // Busca o usuário pelo ID
            if (user != null) {
                session.remove(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error deleting user", e);
        }
    }
}
