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

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

//    public Optional<User> findUserByUserName(String userName) {
//        return userRepository.findUserByUsername(userName);
//    }

    public void deleteUserById(int id) {
         userRepository.deleteUserById(id);
    }
}
