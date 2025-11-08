package com.example.todoappjavafx.controller;

import com.example.todoappjavafx.MainApp;
import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.repository.JsonTaskRepository;
import com.example.todoappjavafx.service.TaskService;
import com.example.todoappjavafx.view.TaskListCell;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {

    @FXML private ListView<Task> taskListView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> priorityFilter;
    @FXML private ProgressBar completionProgress;
    @FXML private Button toggleThemeBtn;
    @FXML private Button addTaskBtn;

    private final TaskService taskService = new TaskService(
            new JsonTaskRepository("src/main/resources/data/tasks.json")
    );

    private final ObservableList<Task> taskObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // We'll call the zero-arg version here for initialization clarity
        refreshTaskListNoArg();
        setupTaskListView(); // ✅ use custom TaskListCell
        setupSearchAndFilter();

        toggleThemeBtn.setOnAction(e -> switchTheme());
        addTaskBtn.setOnAction(e -> openAddTaskDialog());
    }

    /** 🔄 Helper method for initial load (no change) */
    private void refreshTaskListNoArg() {
        taskObservableList.setAll(taskService.getAllTasks());
        taskListView.setItems(taskObservableList);
        updateProgressBar();
    }

    /** 🔄 Refresh task list - NOW ACCEPTS VOID TO MATCH CONSUMER<VOID> IN TaskListCell */
    private void refreshTaskList(Void unused) {
        refreshTaskListNoArg();
    }

    /** 🎨 Custom Card Cell Renderer */
    private void setupTaskListView() {
        taskListView.setCellFactory(listView ->
                // This line now works because refreshTaskList accepts a Void argument
                new TaskListCell(taskService, this::refreshTaskList)
        );
    }

    /** 🔍 Setup search/filter */
    private void setupSearchAndFilter() {
        searchField.textProperty().addListener((obs, oldText, newText) -> filterTasks());
        priorityFilter.getItems().setAll("Low", "Medium", "High");
        priorityFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTasks());
    }

    /** 🔎 Filter logic */
    private void filterTasks() {
        String query = searchField.getText().toLowerCase();
        String selectedPriority = priorityFilter.getValue();

        taskObservableList.setAll(taskService.getAllTasks().stream()
                .filter(task -> task.getTitle().toLowerCase().contains(query))
                .filter(task -> selectedPriority == null ||
                        task.getPriority().name().equalsIgnoreCase(selectedPriority))
                .collect(Collectors.toList()));

        updateProgressBar();
    }

    /** 📊 Progress Bar Update */
    private void updateProgressBar() {
        long total = taskObservableList.size();
        if (total == 0) {
            completionProgress.setProgress(0);
            return;
        }
        long completed = taskObservableList.stream().filter(Task::isCompleted).count();
        completionProgress.setProgress((double) completed / total);
    }

    /** 🌓 Smooth Theme Switch */
    private void switchTheme() {
        Scene scene = toggleThemeBtn.getScene();
        if (scene == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), scene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);
        fadeOut.setOnFinished(evt -> {
            MainApp.toggleTheme();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), scene.getRoot());
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            if (toggleThemeBtn.getText().contains("🌙")) {
                toggleThemeBtn.setText("☀️ Light Mode");
            } else {
                toggleThemeBtn.setText("🌙 Dark Mode");
            }
        });
        fadeOut.play();
    }

    /** ➕ Add Task Dialog */
    private void openAddTaskDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/todoappjavafx/view/new-task-view.fxml"));
            DialogPane dialogPane = loader.load();
            NewTaskController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Add New Task");

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Task newTask = controller.getNewTask();
                if (newTask != null) {
                    taskService.addTask(newTask);
                    refreshTaskListNoArg();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}