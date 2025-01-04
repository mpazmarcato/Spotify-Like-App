package services;

import repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import model.User;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> saveUser(User user) {
        return userRepository.saveUser(user);
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findUserById(id);
    }

    public List<User> listUsers() {
        return userRepository.listUsers();
    }

    public Optional<User> findUserByUserName(String userName) {
        return userRepository.findUserByUsername(userName);
    }

    public Optional<User> deleteUserById(int id) {
        return userRepository.deleteUserById(id);
    }
}
