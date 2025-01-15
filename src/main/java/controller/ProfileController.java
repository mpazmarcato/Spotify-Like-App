package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;

public class ProfileController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    // Método para definir o nome e e-mail do usuário
    public void setUserData(String name, String email) {
        nameLabel.setText(name);
        emailLabel.setText(email);
    }
}
