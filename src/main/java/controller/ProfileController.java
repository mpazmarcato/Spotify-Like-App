package controller;

import eu.hansolo.tilesfx.Tile;
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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ProfileController {

    // Elementos do FXML
    @FXML
    private ImageView profileImage; // A referência ao ImageView no FXML

    @FXML
    private Button changePictureButton; // Referência ao botão "Alterar Foto"
    @FXML
    private Button editProfileButton; // Referência ao botão "Editar Perfil"
    @FXML
    private Button logoutButton; // Referência ao botão "Sair"

    @FXML
    private Label nameLabel; // Referência ao Label de nome
    @FXML
    private Label emailLabel; // Referência ao Label de e-mail
    @FXML
    private Label usernameLabel; // Referência ao Label de usuário

    // Método para definir o nome e e-mail do usuário
    public void setUserData(String name, String email) {
        nameLabel.setText(name);
        emailLabel.setText(email);
    }

    // Método para inicializar os valores do perfil
    public void initialize() {
        // Valores fictícios para o exemplo
        nameLabel.setText("João Silva");
        emailLabel.setText("joao.silva@email.com");
        usernameLabel.setText("joaosilva123");

        Image image = new Image(getClass().getResourceAsStream("/images/imagehomem.jpg"));
        profileImage.setImage(image);
    }

    @FXML
    private void handleChangePicture() {
        // Criação do seletor de arquivos (FileChooser)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione uma nova foto de perfil");

        // Adicionar filtros para imagens
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        // Mostrar o FileChooser e capturar o arquivo selecionado
        File selectedFile = fileChooser.showOpenDialog(profileImage.getScene().getWindow());

        // Verificar se um arquivo foi selecionado
        if (selectedFile != null) {
            // Definir a nova imagem de perfil
            profileImage.setImage(new Image(selectedFile.toURI().toString()));

            // Exibir um alerta informando que a foto foi alterada com sucesso
            showAlert(Alert.AlertType.INFORMATION, "Foto Alterada", "Sua foto de perfil foi alterada com sucesso!");
        }
    }

    // Ação do botão "Editar Perfil"
    @FXML
    private void handleEditProfile() {
        showAlert(Alert.AlertType.INFORMATION, "Editar Perfil", "Funcionalidade de edição em desenvolvimento.");
    }

    // Ação do botão "Sair"
    @FXML
    private void handleLogout() {
        try {
            // Carregar a próxima tela (MainScreen.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/application.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para exibir alertas
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
