package controller;

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

import java.io.IOException;

public class LoginController {

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

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Erro de Login", "Por favor, preencha todos os campos.");
        } else {
            // Lógica para verificar o login (apenas exemplo simples)
            if (username.equals("admin") && password.equals("admin")) {
                showAlert(AlertType.INFORMATION, "Login Bem-Sucedido", "Bem-vindo, " + username + "!");
                // Validação de login (exemplo simples)
                if ("admin".equals(username) && "admin".equals(password)) {
                    System.out.println("Login bem-sucedido!");

                    // Carregar a próxima tela (MainScreen.fxml)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/application.fxml"));
                    Parent root = loader.load();

                    // Obter o estágio atual e mudar a cena
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    Scene newScene = new Scene(root);
                    stage.setScene(newScene);
                    stage.show();
                } else {
                    System.out.println("Usuário ou senha inválidos.");
                }
            } else {
                showAlert(AlertType.ERROR, "Erro de Login", "Usuário ou senha inválidos.");
            }
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
            // Lógica para salvar o registro (exemplo simples: mensagem de sucesso)
            showAlert(AlertType.INFORMATION, "Cadastro Bem-Sucedido", "Usuário cadastrado com sucesso: " + username);
            handleBackToLogin(); // Volta para a tela de login após cadastrar
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
