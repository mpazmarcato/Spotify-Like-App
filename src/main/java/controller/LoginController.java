package controller;

import exceptions.LoginException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.User;
import repositories.UserRepository;
import services.UserService;
import util.UserSession;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    private final UserRepository userRepository = new UserRepository();
    private final UserService userService = new UserService(userRepository);
    private final UserController userController = new UserController(userService);

    @FXML
    private Pane loginPane; // Painel para a tela de login
    @FXML
    private Pane registerPane; // Painel para a tela de registro
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField registerUsernameField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private Button loginButton;

    @FXML
    public void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            if (username.isEmpty() || password.isEmpty()) {
                throw new LoginException("Por favor, preencha todos os campos.");
            }

            Optional<User> userOptional = userController.findAllUsers()
                    .stream()
                    .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                    .findFirst();

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                User loggedInUser = UserSession.getLoggedInUser(); // Obtém o usuário logado
                user.setLogged(true);
                Optional<User> userLogged = userController.updateUser(user);
                if(userLogged.isPresent()){
                    System.out.println("User logged");
                    showAlert(AlertType.INFORMATION, "Login Bem-Sucedido", "Bem-vindo, " + user.getUsername() + "!");
                    // Carregar a próxima tela (MainScreen.fxml)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/application.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    throw new LoginException("Erro ao logar o usuário.");
                }

            } else {
                throw new LoginException("Usuário ou senha inválidos. Deseja criar um novo usuário?");
            }
        } catch (LoginException e) {
            showAlert(AlertType.ERROR, "Erro de Login", e.getMessage());
        } catch (Exception e) {

            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erro inesperado", "Ocorreu um erro inesperado.");
        }

    }

    @FXML
    public void handleRegisterSwitch() {
        // Alterna para a tela de registro
        loginPane.setVisible(false);
        registerPane.setVisible(true);
    }

    @FXML
    public void handleBackToLogin() {
        // Alterna de volta para a tela de login
        registerPane.setVisible(false);
        loginPane.setVisible(true);
    }

    @FXML
    public void handleRegisterSave() {
        String username = registerUsernameField.getText();
        String email = emailField.getText();
        String password = registerPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Erro no Cadastro", "Por favor, preencha todos os campos.");
        } else {
            User newUser = new User(null, username, password, email);
            Optional<User> savedUser = userController.saveUser(newUser);

            if (savedUser.isPresent()) {
                showAlert(AlertType.INFORMATION, "Cadastro Bem-Sucedido", "Usuário cadastrado com sucesso: " + username);
                handleBackToLogin();
            } else {
                showAlert(AlertType.ERROR, "Erro no Cadastro", "Erro ao salvar o usuário. Tente novamente.");
            }

        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
