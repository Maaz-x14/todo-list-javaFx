package com.example.todoappjavafx.controller;

import com.example.todoappjavafx.MainApp;
import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.repository.JsonTaskRepository;
import com.example.todoappjavafx.service.TaskService;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
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

    // ✅ Injected repository path
    private final TaskService taskService = new TaskService(
            new JsonTaskRepository("src/main/resources/data/tasks.json")
    );

    private final ObservableList<Task> taskObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        refreshTaskList();
        setupContextMenu();
        setupSearchAndFilter();

        toggleThemeBtn.setOnAction(e -> switchTheme());
        addTaskBtn.setOnAction(e -> openAddTaskDialog());
    }

    /** 🔄 Refresh task list */
    private void refreshTaskList() {
        taskObservableList.setAll(taskService.getAllTasks());
        taskListView.setItems(taskObservableList);
        updateProgressBar();
    }

    /** ⚙️ Context Menu (Edit/Delete) */
    private void setupContextMenu() {
        taskListView.setCellFactory(lv -> {
            ListCell<Task> cell = new ListCell<>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);
                    if (empty || task == null) {
                        setText(null);
                    } else {
                        setText(task.getTitle() + " — " + task.getPriority().name());
                    }
                }
            };

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("✏️ Edit");
            editItem.setOnAction(e -> handleEdit(cell.getItem()));

            MenuItem deleteItem = new MenuItem("🗑️ Delete");
            deleteItem.setOnAction(e -> handleDelete(cell.getItem()));

            contextMenu.getItems().addAll(editItem, deleteItem);

            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && !cell.isEmpty()) {
                    contextMenu.show(cell, event.getScreenX(), event.getScreenY());
                } else {
                    contextMenu.hide();
                }
            });

            return cell;
        });
    }

    /** ✏️ Edit Task */
    private void handleEdit(Task task) {
        if (task == null) return;

        TextInputDialog dialog = new TextInputDialog(task.getTitle());
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Edit title for: " + task.getTitle());
        dialog.setContentText("New title:");

        dialog.showAndWait().ifPresent(newTitle -> {
            task.setTitle(newTitle);
            taskService.updateTask(task);
            refreshTaskList();
        });
    }

    /** 🗑️ Delete Task */
    private void handleDelete(Task task) {
        if (task == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Task");
        confirm.setHeaderText("Are you sure you want to delete this task?");
        confirm.setContentText(task.getTitle());

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                taskService.deleteTask(task.getId());
                refreshTaskList();
            }
        });
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
                    refreshTaskList();
                }
            }
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Could not open Add Task dialog");
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }
}
