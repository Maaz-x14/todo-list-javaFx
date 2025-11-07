package com.example.todoappjavafx;

import com.example.todoappjavafx.repository.JsonTaskRepository;
import com.example.todoappjavafx.service.TaskService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/todoappjavafx/main-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("TODO List App 🚀");
        stage.setScene(scene);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
