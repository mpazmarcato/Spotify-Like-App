package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.*;
import services.*;
import repositories.*;

import java.util.List;
import java.util.Optional;

public class AppController {

    // FXML Injections
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button homeButton;
    @FXML private Button libraryButton;
    @FXML private ListView<String> resultListView;
    @FXML private ListView<String> nowPlayingList;
    @FXML private Text currentSongText;
    @FXML private ImageView playlistImage1;
    @FXML private ImageView playlistImage2;
    @FXML private ImageView playlistImage3;
    @FXML private Label contentLabel;
    @FXML private VBox leftSidebar;

    // Controladores de funcionalidades adicionais
    private UserController userController;
    private SongController songController;
    private PlaylistController playlistController;
    private AlbumController albumController;
    private MusicPlayerController musicPlayerController;

    @FXML
    private void initialize() {
        // Inicializando os repositórios
        UserRepository userRepository = new UserRepository();
        SongRepository songRepository = new SongRepository();
        PlaylistRepository playlistRepository = new PlaylistRepository();
        AlbumRepository albumRepository = new AlbumRepository();

        // Inicializando os controladores com os serviços apropriados
        UserService userService = new UserService(userRepository);
        SongService songService = new SongService(songRepository);
        PlaylistService playlistService = new PlaylistService(playlistRepository);
        AlbumService albumService = new AlbumService(albumRepository);

        userController = new UserController(userService);
        songController = new SongController(songService);
        playlistController = new PlaylistController(playlistService);
        albumController = new AlbumController(albumService);
        musicPlayerController = new MusicPlayerController();

        // Configurando os listeners de eventos
        searchButton.setOnAction(event -> handleSearchButtonClick());
        homeButton.setOnAction(event -> handleHomeButtonClick());
        libraryButton.setOnAction(event -> handleLibraryButtonClick());

        // Configurando as imagens das playlists com eventos de clique
        playlistImage1.setOnMouseClicked(this::handlePlaylistImageClick);
        playlistImage2.setOnMouseClicked(this::handlePlaylistImageClick);
        playlistImage3.setOnMouseClicked(this::handlePlaylistImageClick);

        // Inicializando o player de música
        musicPlayerController.initializePlayer();
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
        // songController.searchPodcasts(query);
        // updateSearchResults(podcastController.getPodcastResults());
    }

    private void updateSearchResults(String[] results) {
        resultListView.getItems().clear();
        if (results != null && results.length > 0) {
            for (String result : results) {
                resultListView.getItems().add(result);
            }
        } else {
            resultListView.getItems().add("Nenhum resultado encontrado.");
        }
    }

    // Lógica do botão "Home"
    @FXML
    private void handleHomeButtonClick() {
        contentLabel.setText("Bem-vindo à Home!");
        // Aqui você pode implementar a navegação para a tela principal
    }

    // Lógica do botão "Sua Biblioteca"
    @FXML
    private void handleLibraryButtonClick() {
        contentLabel.setText("Sua Biblioteca");
        // Aqui você pode exibir as músicas e playlists do usuário - TODO: esse método existe também;
       //updateLibraryView(playlistController.findAllPlaylists());
    }

    private void updateLibraryView(String[] libraryItems) {
        resultListView.getItems().clear();
        if (libraryItems != null && libraryItems.length > 0) {
            for (String item : libraryItems) {
                resultListView.getItems().add(item);
            }
        } else {
            resultListView.getItems().add("Sua biblioteca está vazia.");
        }
    }

    // Lógica de criação de Podcast
    @FXML
    private void handleCreatePodcast() {
        System.out.println("Criando Podcast...");

        //TODO: o méotodo de criar podcast ja existe(saveSong), basta selecionar o type do Song como podcast

        // Exemplo de criação de um Podcast
//        // Supondo que o PodcastController tenha um método 'createPodcast'
//
//        // Abre uma tela de criação de podcast (pode ser uma tela FXML)
//        TextInputDialog podcastDialog = new TextInputDialog();
//        podcastDialog.setTitle("Criar Podcast");
//        podcastDialog.setHeaderText("Insira o nome do seu podcast");
//        podcastDialog.setContentText("Nome:");
//
//        Optional<String> result = podcastDialog.showAndWait();
//        result.ifPresent(podcastName -> {
//            // Suponha que o PodcastController tenha um método para criar o podcast
//            podcastController.savePodcast(podcastName);
//            contentLabel.setText("Podcast '" + podcastName + "' criado com sucesso!");
//        });
    }

    // Lógica de criação de Playlist
    @FXML
    private void handleCreatePlaylist() {
        System.out.println("Criando Playlist...");

        // Exibe um diálogo para o usuário inserir o nome da playlist
        TextInputDialog playlistDialog = new TextInputDialog();
        playlistDialog.setTitle("Criar Playlist");
        playlistDialog.setHeaderText("Insira o nome da sua playlist");
        playlistDialog.setContentText("Nome da Playlist:");

        Optional<String> playlistNameResult = playlistDialog.showAndWait();
        playlistNameResult.ifPresent(playlistName -> {
            // Agora, pedindo a descrição da playlist
            TextInputDialog descriptionDialog = new TextInputDialog();
            descriptionDialog.setTitle("Adicionar Descrição");
            descriptionDialog.setHeaderText("Insira uma descrição para a playlist");
            descriptionDialog.setContentText("Descrição:");

            Optional<String> descriptionResult = descriptionDialog.showAndWait();
            descriptionResult.ifPresent(description -> {
                // Agora, associando a playlist ao usuário logado (supondo que exista um usuário)
//                User loggedInUser = userController.getLoggedInUser(); // Método que retorna o usuário logado - preciso pegar o usuario logado? podemos fazer isso apenas pegando seu username do front e ai fazer uma busca por username.
                // para pegar o usuario logado irei precisar de informações do front de todo jeito.
//                if (loggedInUser != null) {
//                    Playlist newPlaylist = new Playlist(0, playlistName, 0, description, loggedInUser);
//
//                    // Adicionar músicas à playlist (caso o usuário queira adicionar)
//                    // Aqui você pode adicionar músicas à playlist, por exemplo, solicitando ao usuário
//                    // para selecionar músicas ou pegando automaticamente da biblioteca do usuário.
//
//                    // Exemplo de como adicionar músicas (essa parte pode ser expandida conforme necessário)
//                    List<Song> userSongs = songController.getUserSongs(loggedInUser); // Método que retorna músicas do usuário - musicas do usuario ou as playlists do usuario?
//                    if (!userSongs.isEmpty()) {
//                        newPlaylist.getSongs().addAll(userSongs);
//                    }
//
//                    // Chamando o método do PlaylistController para salvar a playlist
//                    playlistController.savePlaylist(newPlaylist);
//                    contentLabel.setText("Playlist '" + playlistName + "' criada com sucesso!");
//                } else {
//                    contentLabel.setText("Erro: Nenhum usuário logado.");
//                }
            });
        });
    }



    // Lógica para interagir com as playlists recomendadas
    @FXML
    private void handlePlaylistImageClick(MouseEvent event) {
        ImageView clickedImage = (ImageView) event.getSource();
        System.out.println("Playlist clicada: " + clickedImage.getId());
        // Aqui você pode abrir uma tela de detalhes ou tocar a playlist clicada
        //playlistController.playPlaylist(clickedImage.getId());
    }

    // Atualizar a lista "Tocando Agora"
    public void updateNowPlaying(String songName) {
        currentSongText.setText("Tocando agora: " + songName);
    }

    // Controlador do player de música
    private static class MusicPlayerController {

        public void initializePlayer() {
            System.out.println("Inicializando o Player de Música...");
        }

        public void play() {
            System.out.println("Tocando a música...");
            // Lógica para tocar música
        }

        public void pause() {
            System.out.println("Pausando a música...");
            // Lógica para pausar a música
        }

        public void skip() {
            System.out.println("Pulando para a próxima música...");
            // Lógica para pular para a próxima música
        }
    }

    // Métodos para controlar a navegação
    public void playMusic(String songId) {
        musicPlayerController.play();
        updateNowPlaying("Tocando: " + songId);
    }

    public void pauseMusic() {
        musicPlayerController.pause();
        currentSongText.setText("Música pausada");
    }

    public void skipMusic() {
        musicPlayerController.skip();
        currentSongText.setText("Música pulada");
    }
}
