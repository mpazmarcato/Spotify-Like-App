package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class AppController {

    @FXML
    private ListView<String> playlistView;

    @FXML
    private void initialize() {
        playlistView.getItems().addAll("Playlist 1", "Playlist 2", "Playlist 3");
    }

    @FXML
    private void handlePlayMusic() {
        // Implementar o código para tocar música selecionada.
        System.out.println("Playing selected music");
    }
}
