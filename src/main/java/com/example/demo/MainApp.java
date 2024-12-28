package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tela de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        StackPane root = loader.load();
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("SpotifyLikeApp - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
