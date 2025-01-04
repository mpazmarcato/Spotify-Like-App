package repositories;

import java.io.*;
import java.util.List;
import java.util.Optional;

import model.User;
import DAO.BancoDAO;

public class UserRepository {
    private BancoDAO banco;
    private static final String FILE_NAME = "user.dat";

    public UserRepository() {
        banco = BancoDAO.getIntance();
        loadUsers();
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            List<User> users = (List<User>) ois.readObject();
            banco.getArrayUsers().clear();
            banco.getArrayUsers().addAll(users);
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado, inicializando lista de usuários vazia.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveUsersFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(banco.getArrayUsers());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> saveUser(User user) {
        if (banco.getArrayUsers().stream().anyMatch(u -> u.getUserID() == user.getUserID())) {
            System.out.println("Usuário com ID já existente.");
            return Optional.empty();
        }
        try {
            banco.getArrayUsers().add(user);
            saveUsersFile();
            return Optional.of(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public List<User> listUsers() {
        return banco.getArrayUsers();
    }

    public Optional<User> findUserById(int id) {
        return banco.getArrayUsers().stream().filter(user -> user.getUserID() == id).findFirst();
    }

    public Optional<User> findUserByUsername(String username) {
        return banco.getArrayUsers().stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    public Optional<User> deleteUserById(int id) {
        Optional<User> user = findUserById(id);
        user.ifPresent(value -> banco.getArrayUsers().remove(value));
        saveUsersFile();
        return user;
    }
}
