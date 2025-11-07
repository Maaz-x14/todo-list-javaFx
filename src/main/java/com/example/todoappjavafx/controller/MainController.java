package com.example.todoappjavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button toggleThemeButton;

    @FXML
    private void initialize() {
        toggleThemeButton.setOnAction(event -> toggleTheme());
    }

    private void toggleTheme() {
        // Placeholder for theme toggle logic (we’ll add dark mode CSS later)
        System.out.println("Dark mode toggle clicked!");
    }
}
