package controller;

import model.User;
import services.UserService;

import java.util.List;
import java.util.Optional;


public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Optional<User> saveUser(User user) {
        return userService.saveUser(user);
    }

    public Optional<User> findUserById(int id) {
        return userService.findUserById(id);
    }

    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    public Optional<User> updateUser(User user) {
        return userService.updateUser(user);
    }

    public void deleteUserById(int id) {
        userService.deleteUserById(id);
    }
}
