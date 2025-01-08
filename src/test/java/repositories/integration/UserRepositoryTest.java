package repositories.integration;

import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.UserRepository;
import util.HibernateUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private static UserRepository userRepository;
    private User testUser;

    @BeforeAll
    static void setUpClass() {
        userRepository = new UserRepository();
    }

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
    }

    @AfterEach
    void cleanUp() {
        // Clean up any test data after each test
        try {
            if (testUser != null && testUser.getUserID() != null) {
                userRepository.deleteUserById(testUser.getUserID());
            }
        } catch (Exception e) {
            // Log or handle cleanup errors
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    void testSaveUser() {
        Optional<User> savedUser = userRepository.saveUser(testUser);
        assertTrue(savedUser.isPresent());
        assertEquals("testuser", savedUser.get().getUsername());
        testUser = savedUser.get();
    }

    @Test
    void testFindUserById() {
        Optional<User> savedUser = userRepository.saveUser(testUser);
        assertTrue(savedUser.isPresent());
        testUser = savedUser.get();

        Optional<User> foundUser = userRepository.findUserById(testUser.getUserID());
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void testUpdateUser() {
        Optional<User> savedUser = userRepository.saveUser(testUser);
        assertTrue(savedUser.isPresent());
        testUser = savedUser.get();

        testUser.setPassword("newpassword");
        Optional<User> updatedUser = userRepository.updateUser(testUser);
        assertTrue(updatedUser.isPresent());
        assertEquals("newpassword", updatedUser.get().getPassword());
    }

    @Test
    void testDeleteUserById() {
        Optional<User> savedUser = userRepository.saveUser(testUser);
        assertTrue(savedUser.isPresent());
        testUser = savedUser.get();

        Integer userId = testUser.getUserID();
        userRepository.deleteUserById(userId);

        Optional<User> deletedUser = userRepository.findUserById(userId);
        assertFalse(deletedUser.isPresent());
        testUser = null;
    }

    @Test
    void testSaveUserFails() {
        Optional<User> result = userRepository.saveUser(null);
        assertFalse(result.isPresent(), "Expected saveUser to fail and return Optional.empty");
    }

    @Test
    void testFindUserByIdFails() {
        Optional<User> user = userRepository.findUserById(-1);
        assertFalse(user.isPresent(), "Expected findUserById to return Optional.empty for non-existent ID");
    }

    @Test
    void testFindUserByUsernameFails() {
        Optional<User> user = userRepository.findUserByUsername("nonexistentuser");
        assertFalse(user.isPresent(), "Expected findUserByUsername to return Optional.empty for non-existent username");
    }

    @Test
    void testUpdateUserFails() {
        User invalidUser = new User();
        invalidUser.setUserID(-1);
        Optional<User> result = userRepository.updateUser(invalidUser);
        assertFalse(result.isPresent(), "Expected updateUser to fail and return Optional.empty for non-existent user");
    }

    @Test
    void testDeleteUserByIdFails() {
        assertDoesNotThrow(() -> userRepository.deleteUserById(-1),
                "Expected deleteUserById to handle non-existent ID gracefully");
    }

//    @Test
//    void testDatabaseConnectionFails() {
//        HibernateUtil.shutdown();
//
//        Optional<User> result = userRepository.saveUser(testUser);
//        assertFalse(result.isPresent(), "Expected saveUser to fail due to closed database connection");
//
//        userRepository = new UserRepository();
//    }
}