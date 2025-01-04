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

    public List<User> listUsers() {
        return userService.listUsers();
    }

    public Optional<User> findUserByUserName(String userName) {
        return userService.findUserByUserName(userName);
    }

    public Optional<User> deleteUserById(int id) {
        return userService.deleteUserById(id);
    }
}
