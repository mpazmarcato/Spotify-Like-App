package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Aqui você poderia validar o login com a base de dados.
        if (username.equals("admin") && password.equals("admin")) {
            // Redirecionar para a tela principal após o login.
            System.out.println("Login successful");
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Invalid credentials");
            alert.setContentText("Please check your username and password.");
            alert.showAndWait();
        }
    }
}