package controller;

import exceptions.LoginException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.*;
import services.*;
import repositories.*;
import util.UserSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppController {
    private final UserRepository userRepository = new UserRepository();
    private final UserService userService = new UserService(userRepository);
    private final UserController userController = new UserController(userService);

    private final PlaylistRepository playlistRepository = new PlaylistRepository();
    private final PlaylistService playlistService = new PlaylistService(playlistRepository);
    private final PlaylistController playlistController = new PlaylistController(playlistService);

    private final SongRepository songRepository = new SongRepository();
    private final SongService songService = new SongService(songRepository);
    private final SongController songController = new SongController(songService);

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button leaveButton;

    @FXML
    private Button libraryButton;

    @FXML
    private ListView<String> playlistList;

    @FXML
    private ListView<String> songList;

    @FXML
    private ListView<String> nowPlayingList;

    @FXML
    private ListView<String> recentSongsList;

    @FXML
    private Label nameLabel;

    @FXML
    private Button toggleButton;

    @FXML
    private Label timeLabel;

    @FXML
    private Slider timeSlider;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private Label contentLabel;

    private List<File> playlist;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = -1;

    @FXML
    private void initialize() {
        // Verifique se a ListView foi inicializada
        if (recentSongsList != null) {
            recentSongsList.getItems().clear();  // Limpa a lista de músicas recentes na inicialização
        } else {
            System.out.println("recentSongsList não foi inicializado corretamente.");
        }

        loadPlaylists();  // Carrega as playlists

        // Listener para o timeSlider
        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> handleTimeSliderChange());
    }

    private void loadPlaylists() {
        File mediaDir = new File("src/main/java/media");  // Caminho para a pasta 'media'
        playlist = new ArrayList<>();

        if (mediaDir.exists() && mediaDir.isDirectory()) {
            // Percorre todas as subpastas dentro de 'media'
            for (File genreFolder : mediaDir.listFiles()) {
                if (genreFolder.isDirectory()) {  // Se for uma pasta (por exemplo, Rap, Pop, etc.)
                    for (File artistFolder : genreFolder.listFiles()) {
                        if (artistFolder.isDirectory()) {  // Se for uma subpasta do artista (por exemplo, Eminem)
                            for (File songFile : artistFolder.listFiles()) {
                                if (songFile.isFile() && isValidMusicFile(songFile)) {  // Se for um arquivo de música válido
                                    playlist.add(songFile);  // Adiciona o arquivo à playlist
                                    songList.getItems().add(songFile.getName());  // Exibe o nome da música na ListView
                                }
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("Diretório de mídia não encontrado.");
        }
    }

    private boolean isValidMusicFile(File file) {
        String[] validExtensions = {".mp3", ".wav", ".flac"};
        for (String ext : validExtensions) {
            if (file.getName().toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    // Lógica de busca
    @FXML
    private void handleSearchButtonClick() {
        String searchQuery = searchField.getText();
        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Buscando músicas, playlists ou podcasts
            if (searchQuery.contains("playlist")) {
                searchPlaylists();
            } else if (searchQuery.contains("song")) {
                searchSongs();
            } else if (searchQuery.contains("podcast")) {
                searchPodcasts(searchQuery);
            } else {
                contentLabel.setText("Tipo de busca não identificado.");
            }
        } else {
            contentLabel.setText("Por favor, insira uma busca.");
        }
    }

    private void searchPlaylists() {
        contentLabel.setText("Buscando playlists...");
        playlistController.findAllPlaylists();
    }

    private void searchSongs() {
        contentLabel.setText("Buscando músicas...");
        songController.findAllSongs();
    }

    private void searchPodcasts(String query) {
        contentLabel.setText("Buscando podcasts...");
        // Supondo que haja um método de busca de podcasts (não implementado aqui)
    }

    @FXML
    void handleCreatePlaylist(ActionEvent event) {
        System.out.println("Função Criar Playlist em desenvolvimento.");
    }

    @FXML
    void handleLibraryButton(ActionEvent event) {
        System.out.println("Library button clicked!");
    }

    @FXML
    void handleCreatePodcast(ActionEvent event) {
        System.out.println("Create Podcast button clicked!");
    }

    // NÃO MEXA AQUI
    @FXML
    void handleSongSelection() {
        String selectedSong = songList.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            for (int i = 0; i < playlist.size(); i++) {
                if (playlist.get(i).getName().equals(selectedSong)) {
                    currentSongIndex = i;
                    playSong();
                    break;
                }
            }
        }
    }

    @FXML
    void handleTogglePlayPause(ActionEvent event) {
        if (mediaPlayer == null) {
            playSong();
        } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    private void playSong() {
        if (currentSongIndex >= 0 && currentSongIndex < playlist.size()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            Media media = new Media(playlist.get(currentSongIndex).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();

            // Limpa a ListView de "Tocando Agora" e adiciona a música que está tocando
            nowPlayingList.getItems().clear();  // Limpa antes de adicionar a nova música
            nowPlayingList.getItems().add(playlist.get(currentSongIndex).getName());  // Adiciona o nome da música

            // Exibe a música nas últimas tocadas (opcional)
            updateRecentSongs(playlist.get(currentSongIndex).getName());

            // Configura a ação para a próxima música quando a atual terminar
            mediaPlayer.setOnEndOfMedia(this::handleNextSong);

            // Defina o tempo máximo da barra de progresso
            timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());

            // Atualiza a cada segundo
            updateMusicProgress();
        }
    }

    @FXML
    private void handleNextSong() {
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        playSong();
    }

    @FXML
    private void handlePreviousSong(ActionEvent event) {
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        playSong();
    }

    @FXML
    void handleLeaveButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Login.fxml"));
            Parent loginScreen = loader.load();

            Scene loginScene = new Scene(loginScreen, 800, 600);

            String css = getClass().getResource("/com/example/demo/styles.css").toExternalForm();
            loginScene.getStylesheets().add(css);

            javafx.stage.Stage stage = (javafx.stage.Stage) leaveButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Spotify Application");

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleProfileButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/profile.fxml"));
            Parent root = loader.load(); // Carrega o conteúdo do FXML

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Tratar exceções caso o arquivo FXML não seja encontrado ou haja outro erro
        }
    }

    private void updateRecentSongs(String songName) {
        if (recentSongsList != null) {
            recentSongsList.getItems().add(0, songName);  // Evita a exceção de NullPointer
            if (recentSongsList.getItems().size() > 3) {
                recentSongsList.getItems().remove(3);
            }
        } else {
            System.out.println("recentSongsList está null! Verifique o FXML.");
        }
    }

    private void updateMusicProgress() {
        // Atualizar a cada segundo
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(1),
                        event -> {
                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                // Atualiza o valor da barra de progresso (timeSlider)
                                timeSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());

                                // Atualiza o label de tempo (timeLabel)
                                int currentSeconds = (int) mediaPlayer.getCurrentTime().toSeconds();
                                int minutes = currentSeconds / 60;
                                int seconds = currentSeconds % 60;
                                timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
                            }
                        }
                )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handleTimeSliderChange() {
        if (mediaPlayer != null) {
            // Ajustar a posição atual da música
            mediaPlayer.seek(javafx.util.Duration.seconds(timeSlider.getValue()));

            // Atualizar o tempo no label após o usuário arrastar o slider
            int currentSeconds = (int) timeSlider.getValue();
            int minutes = currentSeconds / 60;
            int seconds = currentSeconds % 60;
            timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }
}
