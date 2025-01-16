package controller;

import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import model.Song;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
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
    private File mediaFolder;

    // Elementos da UI
    @FXML
    private Slider timeSlider; // Slider de tempo
    @FXML
    private Label timeLabel; // Label para o tempo (ex: 00:00)

    // Player de música
    private MediaPlayer mediaPlayer;

    // Construtor
    public MusicPlayerController() {
        this.genreSongs = new HashMap<>();
        this.currentGenre = "";
        this.currentPlaylist = new ArrayList<>();
        this.currentSongIndex = -1;
        this.isPlaying = false;
        this.mediaFolder = new File("src/main/java/media");
    }

    // Atualizar o tempo da música no label
    private void updateTimeLabel(Duration currentTime) {
        int minutes = (int) currentTime.toMinutes();
        int seconds = (int) currentTime.toSeconds() % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // Atualizar a posição do slider
    private void updateSlider(Duration currentTime) {
        if (mediaPlayer != null) {
            timeSlider.setValue(currentTime.toSeconds());
        }
    }

    // Mover a música para um novo tempo usando o slider
    public void seekTo(double value) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(value));
        }
    }

    // Atualiza o slider e o label de tempo enquanto a música toca
    private void updateProgress() {
        double currentTimeInSeconds = mediaPlayer.getCurrentTime().toSeconds();
        timeSlider.setValue(currentTimeInSeconds);
        updateTimeLabel(currentTimeInSeconds);
    }

    // Atualiza o label de tempo para o formato mm:ss
    private void updateTimeLabel(double currentTimeInSeconds) {
        int minutes = (int) currentTimeInSeconds / 60;
        int seconds = (int) currentTimeInSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }




}
