package com.example.todoappjavafx.controller;

import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.repository.JsonTaskRepository;
import com.example.todoappjavafx.service.TaskService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

    private void refreshTasks() {
        taskListView.getItems().setAll(taskService.getAllTasks());
        double progress = taskService.getCompletionProgress() / 100;
        completionProgress.setProgress(progress);
    }

    private void handleAddTask() {
        openTaskForm(null); // null = add mode
    }

    private void openTaskForm(Task existingTask) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/todoappjavafx/task-form-view.fxml"));
            Stage dialogStage = new Stage();
            Scene scene = new Scene(loader.load());
            dialogStage.setTitle(existingTask == null ? "Add Task" : "Edit Task");
            dialogStage.setScene(scene);

            TaskFormController controller = loader.getController();
            controller.setTaskFormListener(task -> {
                if (existingTask == null) {
                    taskService.addTask(task);
                } else {
                    taskService.updateTask(task);
                }
                refreshTasks();
            });

            if (existingTask != null)
                controller.setEditMode(existingTask);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
