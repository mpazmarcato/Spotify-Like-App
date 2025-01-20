package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.User;
import util.UserSession;

import java.io.File;
import java.io.IOException;

public class ProfileController {

    @FXML
    private ImageView profileImage;

    @FXML
    private Button changePictureButton;

    @FXML
    private Button editProfileButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    public void initialize() {
        User loggedInUser = UserSession.getLoggedInUser();

        if (loggedInUser != null) {
            nameLabel.setText(loggedInUser.getUsername()); // Display the username
            emailLabel.setText(loggedInUser.getEmail());   // Display the email
        }
        // Set the default profile image
        Image image = new Image(getClass().getResourceAsStream("/images/imagehomem.jpg"));
        profileImage.setImage(image);
    }

    @FXML
    private void handleChangePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione uma nova foto de perfil");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(profileImage.getScene().getWindow());

        if (selectedFile != null) {
            profileImage.setImage(new Image(selectedFile.toURI().toString()));
            showAlert(Alert.AlertType.INFORMATION, "Foto Alterada", "Sua foto de perfil foi alterada com sucesso!");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Load the next screen (MainScreen.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/application.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) nameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
