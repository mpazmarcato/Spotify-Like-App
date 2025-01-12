package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class AppController {

    @FXML
    private ListView<String> nowPlayingList;

    private List<MediaPlayer> mediaPlayers = new ArrayList<>();

    // Método para carregar músicas das subpastas dentro de "media"
    public void loadMusic() {
        String mediaFolderPath = "src/main/java/media"; // Caminho para a pasta de mídia

        File folder = new File(mediaFolderPath);
        if (folder.exists() && folder.isDirectory()) {
            // Carregar músicas recursivamente
            loadMusicFromDirectory(folder);
        }
    }

    // Método recursivo para carregar músicas de todas as subpastas
    private void loadMusicFromDirectory(File folder) {
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    // Se for uma pasta, chama a função recursivamente
                    loadMusicFromDirectory(file);
                } else if (file.getName().endsWith(".mp3")) {
                    // Se for um arquivo .mp3, adiciona à lista
                    String filePath = file.toURI().toString();
                    String songName = file.getName(); // Nome da música sem o caminho completo

                    // Adiciona a música à lista
                    nowPlayingList.getItems().add(songName);

                    // Cria um MediaPlayer para a música
                    Media media = new Media(filePath);
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayers.add(mediaPlayer);
                }
            }
        }
    }

    // Método para tocar a música selecionada
    @FXML
    private void playSelectedSong(MouseEvent event) {
        String selectedSong = nowPlayingList.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            // Encontre o índice da música selecionada
            int index = nowPlayingList.getSelectionModel().getSelectedIndex();
            MediaPlayer selectedMediaPlayer = mediaPlayers.get(index);

            // Parar qualquer música em execução e tocar a selecionada
            for (MediaPlayer player : mediaPlayers) {
                player.stop();
            }
            selectedMediaPlayer.play();
        }
    }

    @FXML
    public void initialize() {
        // Carrega as músicas ao inicializar a tela
        loadMusic();

        // Ação de clique para tocar a música selecionada
        nowPlayingList.setOnMouseClicked(this::playSelectedSong);
    }
}
