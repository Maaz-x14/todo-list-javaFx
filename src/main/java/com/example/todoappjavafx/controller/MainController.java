package com.example.todoappjavafx.controller;

import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.repository.JsonTaskRepository;
import com.example.todoappjavafx.service.TaskService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {

    @FXML private ListView<Task> taskListView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> priorityFilter;
    @FXML private ProgressBar completionProgress;
    @FXML private Button toggleThemeBtn;
    @FXML private Button addTaskBtn;

    private TaskService taskService;
    private boolean darkMode = false;

    @FXML
    public void initialize() {
        taskService = new TaskService(new JsonTaskRepository("tasks.json"));
        refreshTasks();
        setupEventListeners();
    }

    private void setupEventListeners() {
        toggleThemeBtn.setOnAction(e -> toggleTheme());
        addTaskBtn.setOnAction(e -> handleAddTask());
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        String theme = darkMode ? "Dark Mode 🌑" : "Light Mode ☀️";
        toggleThemeBtn.setText(theme);
        // (We’ll apply actual dark/light CSS later)
    }

    private void handleAddTask() {
        System.out.println("Add Task clicked!");
        // (We’ll add dialog/modal next)
    }

    private void refreshTasks() {
        taskListView.getItems().setAll(taskService.getAllTasks());
        double progress = taskService.getCompletionProgress() / 100;
        completionProgress.setProgress(progress);
    }
}
