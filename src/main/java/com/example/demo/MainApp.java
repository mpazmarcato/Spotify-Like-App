package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);

        // Adicionando o CSS
        String css = getClass().getResource("/com/example/demo/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Spotify Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
