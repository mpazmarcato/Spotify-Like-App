package controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MusicPlayerController {

    private Map<String, List<Song>> genreSongs;
    private String currentGenre;
    private List<Song> currentPlaylist;
    private int currentSongIndex;
    private boolean isPlaying;
    private MediaPlayer mediaPlayer;
    private File mediaFolder;

    // Construtor
    public MusicPlayerController() {
        this.genreSongs = new HashMap<>();
        this.currentGenre = "";
        this.currentPlaylist = new ArrayList<>();
        this.currentSongIndex = -1;
        this.isPlaying = false;
        this.mediaFolder = new File("src/main/java/media");
    }

    // Inicializa o player de música e carrega músicas de todas as subpastas
    public void initializePlayer() {
        System.out.println("Inicializando o Player de Música...");

        // Verifica se a pasta de mídia existe
        if (mediaFolder.exists() && mediaFolder.isDirectory()) {
            for (File genreFolder : mediaFolder.listFiles()) {
                if (genreFolder.isDirectory()) {
                    List<Song> genreList = new ArrayList<>();
                    for (File file : genreFolder.listFiles()) {
                        if (file.isFile() && file.getName().endsWith(".mp3")) {
                            genreList.add(new Song());
                        }
                    }
                    genreSongs.put(genreFolder.getName(), genreList);
                }
            }

            if (genreSongs.isEmpty()) {
                System.out.println("Nenhuma música encontrada na pasta de mídia.");
            }
        } else {
            System.out.println("A pasta de mídia não existe ou não é um diretório.");
        }
    }

    // Seleciona e começa a tocar as músicas de um determinado gênero
    public void playGenre(String genre) {
        if (genreSongs.containsKey(genre)) {
            currentGenre = genre;
            currentPlaylist = genreSongs.get(genre);
            currentSongIndex = 0; // Começar da primeira música
            playSong(currentPlaylist.get(currentSongIndex)); // Começar a tocar a primeira música do gênero
        } else {
            System.out.println("Gênero não encontrado: " + genre);
        }
    }

    // Método para tocar a música
    public void play() {
        if (currentPlaylist.isEmpty()) {
            System.out.println("Não há músicas na fila para tocar.");
            return;
        }

        if (!isPlaying && currentSongIndex == -1) {
            currentSongIndex = 0; // Começar a tocar a primeira música
            playSong(currentPlaylist.get(currentSongIndex));
        } else if (!isPlaying) {
            System.out.println("Retomando a música: " + currentPlaylist.get(currentSongIndex).getTitle());
            mediaPlayer.play();
            isPlaying = true;
        } else {
            System.out.println("Já está tocando a música: " + currentPlaylist.get(currentSongIndex).getTitle());
        }
    }

    // Método para pausar a música
    public void pause() {
        if (isPlaying) {
            System.out.println("Pausando a música: " + currentPlaylist.get(currentSongIndex).getTitle());
            mediaPlayer.pause();
            isPlaying = false;
        } else {
            System.out.println("Não há música tocando no momento.");
        }
    }

    // Método para pular para a próxima música
    public void skip() {
        if (currentPlaylist.isEmpty()) {
            System.out.println("Não há músicas na fila para pular.");
            return;
        }

        if (currentSongIndex < currentPlaylist.size() - 1) {
            currentSongIndex++;
            playSong(currentPlaylist.get(currentSongIndex));
        } else {
            System.out.println("Você já está na última música da fila.");
        }
    }

    // Método para voltar à música anterior
    public void previous() {
        if (currentPlaylist.isEmpty()) {
            System.out.println("Não há músicas na fila para voltar.");
            return;
        }

        if (currentSongIndex > 0) {
            currentSongIndex--;
            playSong(currentPlaylist.get(currentSongIndex));
        } else {
            System.out.println("Você já está na primeira música da fila.");
        }
    }

    // Método para reproduzir uma música específica
    private void playSong(Song song) {
        System.out.println("Tocando: " + song.getTitle());
        Media media = new Media(new File(song.getPath()).toURI().toString()); // Carregar o arquivo de áudio
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> skip()); // Reproduz a próxima música automaticamente quando a atual terminar
        mediaPlayer.play();
        isPlaying = true;
    }

    // Exibe a música atual que está tocando
    public void showCurrentSong() {
        if (currentSongIndex != -1) {
            System.out.println("Música atual: " + currentPlaylist.get(currentSongIndex).getTitle());
        } else {
            System.out.println("Nenhuma música está tocando no momento.");
        }
    }

}
