package controller;

import exceptions.LoginException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

    private List<File> playlist;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = -1;

    @FXML
    private void initialize() {
        if (recentSongsList != null) {
            recentSongsList.getItems().clear();
        } else {
            System.out.println("recentSongsList não foi inicializado corretamente.");
        }

        loadPlaylists();

        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> handleTimeSliderChange());
    }

    private boolean isPlayableMedia(File file) {
        try {
            Media testMedia = new Media(file.toURI().toString());
            MediaPlayer testPlayer = new MediaPlayer(testMedia);
            testPlayer.dispose();
            return true;
        } catch (Exception e) {
            System.err.println("Arquivo inválido ou corrompido: " + file.getName());
            return false;
        }
    }

    private void loadPlaylists() {
        File mediaDir = new File("src/main/java/media");
        playlist = new ArrayList<>();

        if (mediaDir.exists() && mediaDir.isDirectory()) {
            for (File genreFolder : mediaDir.listFiles()) {
                if (genreFolder.isDirectory()) {
                    for (File artistFolder : genreFolder.listFiles()) {
                        if (artistFolder.isDirectory()) {
                            for (File songFile : artistFolder.listFiles()) {
                                if (songFile.isFile() && isValidMusicFile(songFile) && isPlayableMedia(songFile)) {
                                    playlist.add(songFile);
                                    songList.getItems().add(songFile.getName());
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
        // Verifica se o índice da música está correto
        if (currentSongIndex < 0 || currentSongIndex >= playlist.size()) {
            System.err.println("Índice da música está fora dos limites da playlist.");
            return;
        }

        // Se o player atual existir, libere-o corretamente
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            // Criação de uma Task para carregar a música em background (sem bloquear a interface)
            Task<Void> loadTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    File currentSong = playlist.get(currentSongIndex);
                    Media media = new Media(currentSong.toURI().toString());

                    mediaPlayer = new MediaPlayer(media);

                    // Desabilitar o buffer automático (deixe o usuário controlar o início)
                    mediaPlayer.setOnReady(() -> {
                        // Configurar o player para começar manualmente (não autoplay)
                        mediaPlayer.seek(javafx.util.Duration.ZERO);  // Começar do início (segundo 0)
                        nowPlayingList.getItems().clear();
                        nowPlayingList.getItems().add(currentSong.getName());
                        timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());  // Atualiza o slider de tempo
                        updateMusicProgress();  // Inicia o progresso da música
                        updateRecentSongs(currentSong.getName());  // Atualiza músicas recentes
                        preBufferNextSong();  // Pré-carregar a próxima música
                        mediaPlayer.play(); // Inicia a reprodução manualmente
                    });

                    // Define o que acontece quando a música terminar
                    mediaPlayer.setOnEndOfMedia(() -> handleNextSong());

                    // Tratamento de erro
                    mediaPlayer.setOnError(() -> {
                        System.err.println("Erro ao tentar reproduzir a música: " + currentSong.getName());
                        System.err.println("Detalhes do erro: " + mediaPlayer.getError());
                        handleNextSong();  // Caso de erro, tenta a próxima música
                    });

                    return null;
                }
            };

            loadTask.setOnFailed(e -> {
                System.err.println("Erro ao carregar música: " + playlist.get(currentSongIndex).getName());
                System.err.println("Detalhes do erro: " + loadTask.getException());
            });

            new Thread(loadTask).start();

        } catch (Exception e) {
            System.err.println("Erro ao carregar a música: " + playlist.get(currentSongIndex).getName());
            e.printStackTrace();
        }
    }

    // Função para pré-bufferizar a próxima música
    private void preBufferNextSong() {
        // Tentando pré-bufferizar de forma eficiente
        int nextSongIndex = (currentSongIndex + 1) % playlist.size();
        if (nextSongIndex != currentSongIndex) {
            File nextSong = playlist.get(nextSongIndex);
            Media nextMedia = new Media(nextSong.toURI().toString());

            // Cria um MediaPlayer para a próxima música, mas sem tocar
            MediaPlayer nextPlayer = new MediaPlayer(nextMedia);

            // Configura o próximo player para pré-bufferizar
            nextPlayer.setOnReady(() -> {
                nextPlayer.dispose(); // Libera recursos após o carregamento
            });

            nextPlayer.setOnError(() -> nextPlayer.dispose()); // Libera se ocorrer erro
            nextPlayer.play(); // Pré-carrega a próxima música
        }
    }

    // Função otimizada para lidar com a transição para a próxima música
    @FXML
    private void handleNextSong() {
        // Se a playlist estiver vazia, não tenta reproduzir música
        if (playlist.isEmpty()) {
            System.err.println("A playlist está vazia!");
            return;
        }

        // Avança para a próxima música (circular)
        currentSongIndex = (currentSongIndex + 1) % playlist.size();

        // Toca a próxima música
        playSong();
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

    private void updateMusicProgress() {
        // Atualiza o progresso da música usando um Timeline
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(1), // Atualiza a cada segundo
                        event -> {
                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                                timeSlider.setValue(currentTime);

                                int currentSeconds = (int) currentTime;
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
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.seek(javafx.util.Duration.seconds(timeSlider.getValue()));

            int currentSeconds = (int) timeSlider.getValue();
            int minutes = currentSeconds / 60;
            int seconds = currentSeconds % 60;
            timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }
}
