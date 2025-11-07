package com.example.todoappjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {

    private static Scene scene;
    private static boolean darkMode = false; // toggle flag

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApp.class.getResource("view/main-view.fxml")
        );
        scene = new Scene(fxmlLoader.load());

        // 👇 Default light theme
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("styles/light-theme.css")
                ).toExternalForm()
        );

        stage.setTitle("TODO App 🚀");
        stage.setScene(scene);
        stage.show();
    }

    // ✅ Utility method for switching themes later
    public static void toggleTheme() {
        scene.getStylesheets().clear();
        String theme = darkMode ? "styles/light-theme.css" : "styles/dark-theme.css";
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        MainApp.class.getResource(theme)
                ).toExternalForm()
        );
        darkMode = !darkMode;
    }

    public static void main(String[] args) {
        launch();
    }
}
