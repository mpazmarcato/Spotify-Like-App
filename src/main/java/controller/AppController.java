package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppController {
    @FXML
    private TableView<Song> songTable;
    @FXML
    private TableColumn<Song, String> titleColumn;
    @FXML
    private TableColumn<Song, String> artistColumn;
    @FXML
    private Button playButton, pauseButton, stopButton;
    @FXML
    private TextField searchField;

    private MediaPlayer mediaPlayer;
    private List<Song> songs;

    public void initialize() {
        songs = new ArrayList<>();
        songs.add(new Song("Song 1", "Artist 1", "Album 1", "C:/Users/mpazm/Downloads/Mozart 07 Lacrimosa.mp3"));
        //songs.add(new Song("Song 2", "Artist 2", "Album 2", "https://www.youtube.com/watch?v=8dnlGs4Rmx0&list=RD16jA-6hiSUo&index=2"));

        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        artistColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getArtist()));
        songTable.getItems().addAll(songs);
    }

    @FXML
    private void playSong() {
        Song selectedSong = songTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            stopSong();
            Media media = new Media(new File(selectedSong.getPath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
    }

    @FXML
    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    @FXML
    private void pauseSong() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @FXML
    private void searchSong() {
        String query = searchField.getText().toLowerCase();
        songTable.getItems().clear();
        songTable.getItems().addAll(
                songs.stream()
                        .filter(song -> song.getTitle().toLowerCase().contains(query) || song.getArtist().toLowerCase().contains(query))
                        .toList()
        );
    }



















































}