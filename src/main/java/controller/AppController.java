package controller;

import exceptions.LoginException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


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
    public Button addSongButton;

    @FXML
    public Button deleteSongButton;

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

    private ObservableList<String> nowPlayingSongs = FXCollections.observableArrayList();


    private List<File> playlist;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = -1;
    private boolean isSliderChanging = false;

    @FXML
    private void initialize() {
        nowPlayingList.setItems(nowPlayingSongs);
        loadPlaylists();

        // Check if mediaPlayer is null before accessing currentTimeProperty
        if (mediaPlayer != null) {
            // Atualiza a música no slider sem interrupções excessivas.
            timeSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                isSliderChanging = newValue;
                if (!isSliderChanging && mediaPlayer != null) {
                    mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                }
            });

            // Quando o valor do slider mudar, ajusta a música sem interromper a reprodução.
            timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer != null && !isSliderChanging) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            });

            // Corrigindo a atualização do tempo de reprodução
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer != null && !isSliderChanging) {
                    double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                    double totalTime = mediaPlayer.getTotalDuration().toSeconds();
                    timeSlider.setValue(currentTime);
                    timeLabel.setText(String.format("%02d:%02d / %02d:%02d",
                            (int) currentTime / 60, (int) currentTime % 60,
                            (int) totalTime / 60, (int) totalTime % 60));
                }
            });
        }
    }


    @FXML
    private void handleSongSelection() {
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
    private void toggleMusic() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            toggleButton.setText("Play");
        } else {
            mediaPlayer.play();
            toggleButton.setText("Pause");
        }
    }

    @FXML
    private void handleNextSong() {
        if (playlist.isEmpty()) {
            System.err.println("A playlist está vazia!");
            return;
        }

        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        playSong();
    }

    @FXML
    private void handlePreviousSong() {
        if (playlist.isEmpty()) {
            System.err.println("A playlist está vazia!");
            return;
        }

        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        playSong();
    }

    @FXML
    private void playSong() {
        if (currentSongIndex < 0 || currentSongIndex >= playlist.size()) {
            System.err.println("Índice da música está fora dos limites da playlist.");
            return;
        }

        // Stop and dispose of the previous mediaPlayer if it exists
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Task<Void> loadSongTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    File currentSong = playlist.get(currentSongIndex);
                    Media media = new Media(currentSong.toURI().toString());
                    mediaPlayer = new MediaPlayer(media);

                    mediaPlayer.setOnReady(() -> {
                        Platform.runLater(() -> {
                            timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                            mediaPlayer.play();
                            toggleButton.setText("Play/Pause");
                            updateRecentSongs(currentSong.getName());

                            // Update now playing list
                            nowPlayingSongs.clear();  // Clear the list before adding new song
                            nowPlayingSongs.add("Now Playing: " + currentSong.getName());  // Add the current song name

                            // Bind the now playing list to the ListView
                            nowPlayingList.setItems(nowPlayingSongs);
                        });

                        // Add the currentTimeProperty listener here
                        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                            if (mediaPlayer != null && !isSliderChanging) {
                                double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                                double totalTime = mediaPlayer.getTotalDuration().toSeconds();

                                Platform.runLater(() -> {
                                    timeSlider.setValue(currentTime);
                                    timeLabel.setText(String.format("%02d:%02d / %02d:%02d",
                                            (int) currentTime / 60, (int) currentTime % 60,
                                            (int) totalTime / 60, (int) totalTime % 60));
                                });
                            }
                        });
                    });

                    mediaPlayer.setOnEndOfMedia(AppController.this::handleNextSong);

                } catch (Exception e) {
                    System.err.println("Erro ao reproduzir a música: " + playlist.get(currentSongIndex).getName());
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(loadSongTask).start(); // Carrega a música em uma thread separada
    }






    private void updateMusicProgress() {
        mediaPlayer.currentTimeProperty().addListener(observable -> {
            if (mediaPlayer != null && !isSliderChanging) {
                timeSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                double totalTime = mediaPlayer.getTotalDuration().toSeconds();
                timeLabel.setText(String.format("%02d:%02d / %02d:%02d",
                        (int) currentTime / 60, (int) currentTime % 60,
                        (int) totalTime / 60, (int) totalTime % 60));
            }
        });
    }

    private void updateRecentSongs(String songName) {
        if (recentSongsList != null) {
            recentSongsList.getItems().add(0, songName);
            if (recentSongsList.getItems().size() > 3) {
                recentSongsList.getItems().remove(3);
            }
        } else {
            System.out.println("recentSongsList está null! Verifique o FXML.");
        }
    }

    @FXML
    private void openFileChooser(ActionEvent event) {
        Window ownerWindow = ((Control) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Adicionar arquivo de áudio");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de áudio", "*.mp3", "*.wav", "*.flac")
        );
        File selectedFile = fileChooser.showOpenDialog(ownerWindow);
        if (selectedFile != null && isPlayableMedia(selectedFile)) {
            playlist.add(selectedFile);
            songList.getItems().add(selectedFile.getName());
        }
    }

    @FXML
    private void openFolderChooser(ActionEvent event) {
        Window ownerWindow = ((Control) event.getSource()).getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Adicionar diretório de músicas");
        File selectedDir = directoryChooser.showDialog(ownerWindow);
        if (selectedDir != null && selectedDir.isDirectory()) {
            for (File file : selectedDir.listFiles()) {
                if (file.isFile() && isPlayableMedia(file)) {
                    playlist.add(file);
                    songList.getItems().add(file.getName());
                }
            }
        }
    }

    private void loadPlaylists() {
        playlist = new ArrayList<>();
        File defaultMusicDir = new File("src/main/resources/media");
        if (defaultMusicDir.exists() && defaultMusicDir.isDirectory()) {
            for (File file : defaultMusicDir.listFiles()) {
                if (file.isFile() && isPlayableMedia(file)) {
                    playlist.add(file);
                    songList.getItems().add(file.getName());
                }
            }
        }
    }

    private boolean isPlayableMedia(File file) {
        try {
            new Media(file.toURI().toString());
            return true;
        } catch (Exception e) {
            System.err.println("Arquivo inválido: " + file.getName());
            return false;
        }
    }

    @FXML
    private void handleTimeSliderChange() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(javafx.util.Duration.seconds(timeSlider.getValue()));
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


    @FXML
    private void handleAddSongButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos de música", "*.mp3", "*.wav", "*.flac"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            if (isValidMusicFile(selectedFile) && isPlayableMedia(selectedFile)) {
                playlist.add(selectedFile);

                songList.getItems().add(selectedFile.getName());

                showAlert("Música adicionada", "A música foi adicionada à playlist com sucesso.");
            } else {
                showAlert("Erro", "O arquivo selecionado não é uma música válida.");
            }
        }
    }

    @FXML
    private void handleDeleteSongButtonClick() {
        // Obtém a música selecionada na lista de músicas
        String selectedSongName = songList.getSelectionModel().getSelectedItem();

        if (selectedSongName == null) {
            showAlert("Erro", "Por favor, selecione uma música para deletar.");
            return;
        }

        // Pergunta se o usuário tem certeza que quer deletar a música
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmação");
        confirmAlert.setHeaderText("Você tem certeza que deseja excluir a música?");
        confirmAlert.setContentText("A música será removida da playlist.");

        // Se o usuário confirmar, deletamos a música
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Encontra a música na playlist e remove
            File songToDelete = null;
            for (File song : playlist) {
                if (song.getName().equals(selectedSongName)) {
                    songToDelete = song;
                    break;
                }
            }

            if (songToDelete != null) {
                // Remove a música da playlist
                playlist.remove(songToDelete);

                // Remove a música da lista na interface
                songList.getItems().remove(selectedSongName);

                showAlert("Música excluída", "A música foi excluída com sucesso.");

//                // Excluir o arquivo fisicamente (opcional)
//                boolean deleted = songToDelete.delete();
//                if (deleted) {
//                    showAlert("Música excluída", "A música foi excluída com sucesso.");
//                } else {
//                    showAlert("Erro", "Não foi possível excluir o arquivo da música.");
//                }
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleSearchButtonClick() {
        String searchQuery = searchField.getText();
        if (searchQuery != null && !searchQuery.isEmpty()) {
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



    @FXML
    private void handlePreviousSong(ActionEvent event) {
        // Se a playlist estiver vazia, não tenta retroceder a música
        if (playlist.isEmpty()) {
            System.err.println("A playlist está vazia!");
            return;
        }

        // Retrocede para a música anterior (circular)
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();

        // Toca a música anterior
        playSong();
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

    @FXML
    void handleLeaveButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Login.fxml"));
            Parent loginScreen = loader.load();

            Scene loginScene = new Scene(loginScreen, 800, 600);
            String css = getClass().getResource("/com/example/demo/styles.css").toExternalForm();
            loginScene.getStylesheets().add(css);

            Stage stage = (Stage) leaveButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Spotify Application");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
