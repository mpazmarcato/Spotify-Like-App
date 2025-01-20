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
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.*;
import repositories.*;
import util.UserSession;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

import javax.swing.text.html.Option;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;


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

    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @FXML
    public Button addSongButton;

    @FXML
    public Button deleteSongButton;

    @FXML
    public ListView playlistListView;

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
    private Button backButton;

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

    @FXML
    private TextField playlistTitleField;

    @FXML
    private TextArea playlistDescriptionArea;

    private Stage primaryStage;  // A janela principal da aplicação

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    private final ObservableList<String> nowPlayingSongs = FXCollections.observableArrayList();


    private List<File> playlist;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = -1;
    private boolean isSliderChanging = false;

    @FXML
    private void initialize() {
        if (playlistList == null) {
            System.out.println("playlistList is null. Check FXML and controller bindings.");
        } else {
            System.out.println("playlistList initialized successfully.");
            updatePlaylistDisplay();
        }

        loadPlaylists();

        if (mediaPlayer != null) {
            timeSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                isSliderChanging = newValue;
                if (!isSliderChanging && mediaPlayer != null) {
                    mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                }
            });

            timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer != null && !isSliderChanging) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            });

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

                            nowPlayingSongs.clear();
                            nowPlayingSongs.add("Now Playing: " + currentSong.getName());
                            if (nowPlayingList != null) {
                                nowPlayingList.setItems(nowPlayingSongs);
                            } else {
                                System.err.println("nowPlayingList está null! Verifique o FXML.");
                            }

                            updateRecentSongs(currentSong.getName());
                        });

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

        new Thread(loadSongTask).start();
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

    private void loadPlaylists() {
        playlist = new ArrayList<>();
        File defaultMusicDir = new File("src/main/resources/media");
        if (defaultMusicDir.exists() && defaultMusicDir.isDirectory()) {
            for (File file : defaultMusicDir.listFiles()) {
                if (file.isFile() && isPlayableMedia(file)) {
                    playlist.add(file);
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
                // Verifica se a música já está na playlist
                if (!playlist.contains(selectedFile)) {
                    playlist.add(selectedFile);
                    songList.getItems().add(selectedFile.getName()); // Aqui, você adiciona o nome da música, não o objeto File
                    showAlert("Música adicionada", "A música foi adicionada à playlist com sucesso.");
                } else {
                    showAlert("Erro", "Essa música já está na playlist.");
                }
            } else {
                showAlert("Erro", "O arquivo selecionado não é uma música válida.");
            }
        }
    }

    @FXML
    private void handleDeleteSongButtonClick() {
        String selectedSongName = songList.getSelectionModel().getSelectedItem();

        if (selectedSongName == null) {
            showAlert("Erro", "Por favor, selecione uma música para deletar.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmação");
        confirmAlert.setHeaderText("Você tem certeza que deseja excluir a música?");
        confirmAlert.setContentText("A música será removida da playlist.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            File songToDelete = null;
            for (File song : playlist) {
                if (song.getName().equals(selectedSongName)) {
                    songToDelete = song;
                    break;
                }
            }

            if (songToDelete != null) {
                playlist.remove(songToDelete);
                songList.getItems().remove(selectedSongName); // Aqui, remove o nome da música
                showAlert("Música excluída", "A música foi excluída com sucesso.");
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
    private void handleCreatePlaylist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/create_playlist.fxml"));
            Parent root = loader.load();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Create New Playlist");
            dialog.setHeaderText("Enter playlist details:");

            TextField playlistTitleField = new TextField();
            TextArea playlistDescriptionArea = new TextArea();

            ListView<Song> availableSongsListView = new ListView<>();
            availableSongsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            // Adiciona as músicas disponíveis à lista
            ObservableList<Song> allSongs = FXCollections.observableArrayList();
            for (File file : playlist) {
                if (isPlayableMedia(file)) {
                    Song song = new Song();
                    song.setTitle(file.getName()); // Adicionando o nome do arquivo como título
                    song.setArtist("Unknown Artist"); // Ajuste conforme necessário
                    allSongs.add(song);
                }
            }

            availableSongsListView.setItems(allSongs);

            availableSongsListView.setCellFactory(param -> new ListCell<Song>() {
                @Override
                protected void updateItem(Song song, boolean empty) {
                    super.updateItem(song, empty);
                    setText((empty || song == null) ? null : song.getTitle() + " - " + song.getArtist());
                }
            });

            VBox content = new VBox(10);
            content.getChildren().addAll(
                    new Label("Title:"),
                    playlistTitleField,
                    new Label("Description:"),
                    playlistDescriptionArea,
                    new Label("Select Songs:"),
                    availableSongsListView
            );

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String title = playlistTitleField.getText();
                String description = playlistDescriptionArea.getText();

                if (title == null || title.trim().isEmpty()) {
                    showAlert("Error", "Please enter a playlist title.");
                    return;
                }

                Playlist newPlaylist = new Playlist();
                newPlaylist.setTitle(title);
                newPlaylist.setDescription(description);

                // Adiciona músicas selecionadas à nova playlist
                List<Song> selectedSongs = availableSongsListView.getSelectionModel().getSelectedItems();
                newPlaylist.getSongs().addAll(selectedSongs);

                // Salva a playlist
                Optional<Playlist> savedPlaylist = playlistController.savePlaylist(newPlaylist);
                if (savedPlaylist.isPresent()) {
                    updatePlaylistDisplay();
                    showAlert("Success", "Playlist created successfully with " + selectedSongs.size() + " songs!");
                } else {
                    showAlert("Error", "Failed to create playlist. Please try again.");
                }

            }
        } catch (IOException e) {
            logger.error("Error creating playlist: ", e);
            showAlert("Error", "An error occurred while creating the playlist.");
        }
    }




    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            // Carrega a próxima tela a partir do arquivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/application.fxml"));
            Parent root = loader.load();

            // Obtém o Stage a partir do evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Configura a nova cena no Stage existente
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.error("Erro ao carregar a tela principal: ", e);
            showAlert("Erro", "Não foi possível carregar a tela principal. Por favor, tente novamente.");
        }
    }

    private void updatePlaylistDisplay() {
        if (playlistList == null) {
            System.err.println("playlistList is null. Check FXML bindings.");
            return;
        }

        playlistList.getItems().clear(); // Limpa os itens existentes
        List<Playlist> playlists = playlistController.findAllPlaylists(); // Obtém todas as playlists
        if (playlists == null) {
            System.err.println("playlists are null. Check the data source.");
            return;
        }

        for (Playlist playlist : playlists) {
            String displayText = playlist.getTitle() + " (" + playlist.getSongs().size() + " songs)";
            playlistList.getItems().add(displayText); // Adiciona playlists à ListView
        }
    }

    @FXML
    private void handleLibraryButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/library.fxml"));
            Parent root = loader.load();

            Stage libraryStage = new Stage();
            libraryStage.setTitle("Your Playlists");
            libraryStage.setScene(new Scene(root, 600, 400));
            libraryStage.show();

            ListView<Playlist> playlistListView = (ListView<Playlist>) root.lookup("#playlistListView");
            Label descriptionLabel = (Label) root.lookup("#descriptionLabel");
            ListView<String> songsListView = (ListView<String>) root.lookup("#songsListView");

            // Obtém as playlists criadas
            ObservableList<Playlist> playlists = FXCollections.observableArrayList(playlistController.findAllPlaylists());

            playlistListView.setItems(playlists);

            // Configura o ListView de playlists
            playlistListView.setCellFactory(param -> new ListCell<Playlist>() {
                @Override
                protected void updateItem(Playlist playlist, boolean empty) {
                    super.updateItem(playlist, empty);
                    setText((empty || playlist == null) ? null : playlist.getTitle());
                }
            });

            // Listener para exibir descrição e músicas da playlist selecionada
            playlistListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    descriptionLabel.setText(newSelection.getDescription());

                    // Aqui você já tem as músicas adicionadas à playlist, então basta extrair os títulos
                    List<String> songTitles = new ArrayList<>();
                    for (Song song : newSelection.getSongs()) {
                        songTitles.add(song.getTitle()); // Adiciona o título da música à lista
                    }

                    // Atualiza a ListView de músicas
                    songsListView.setItems(FXCollections.observableArrayList(songTitles));
                } else {
                    descriptionLabel.setText("");
                    songsListView.setItems(FXCollections.observableArrayList());
                }
            });


        } catch (IOException e) {
            logger.error("Error opening Library: ", e);
            showAlert("Error", "An error occurred while opening the Library.");
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



    @FXML
    private void handlePreviousSong(ActionEvent event) {
        if (playlist.isEmpty()) {
            System.err.println("A playlist está vazia!");
            return;
        }

        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();

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
            showAlert("Erro", "Não foi possível carregar a tela de perfil. Por favor, tente novamente.");
            logger.error("Erro ao carregar a tela de perfil: ", e);
        }
    }

    @FXML
    void handleLeaveButton(ActionEvent event) {
        // Salvar a posição da música e o índice da música
        if (mediaPlayer != null) {
            double currentTime = mediaPlayer.getCurrentTime().toSeconds();
            // Salvar a posição em algum lugar (arquivo, banco de dados, etc.)
            saveMusicState(currentSongIndex, currentTime);

            mediaPlayer.stop();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Login.fxml"));
            Parent loginScreen = loader.load();

            Scene loginScene = new Scene(loginScreen, 800, 600);
            String css = Objects.requireNonNull(getClass().getResource("/com/example/demo/styles.css")).toExternalForm();
            loginScene.getStylesheets().add(css);

            Stage stage = (Stage) leaveButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Spotify Application");
            stage.show();
        } catch (IOException e) {
            logger.error("Erro ao carregar a tela de perfil: ", e);
        }
    }

    private void saveMusicState(int songIndex, double position) {
        // Exemplo de como salvar com Preferences
        Preferences prefs = Preferences.userNodeForPackage(AppController.class);
        prefs.putInt("currentSongIndex", songIndex);
        prefs.putDouble("currentSongPosition", position);
    }

}
