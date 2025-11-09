package com.example.todoappjavafx.view;

import com.example.todoappjavafx.MainApp;
import com.example.todoappjavafx.controller.TaskFormController;
import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.service.TaskService;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.function.Consumer;


public class TaskListCell extends ListCell<Task> {

    @FXML private AnchorPane cardContainer;
    @FXML private Label titleLabel;
    @FXML private Label priorityLabel;
    @FXML private Label descLabel;
    @FXML private Label dueDateLabel;
    @FXML private CheckBox completedCheck;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    private final TaskService taskService;
    private final Consumer<Void> onListChanged;
    private FXMLLoader loader;

    public TaskListCell(TaskService taskService, Consumer<Void> onListChanged) {
        this.taskService = taskService;
        this.onListChanged = onListChanged;
    }

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);

        if (empty || task == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("/com/example/todoappjavafx/view/task-card.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        titleLabel.setText(task.getTitle());
        if (descLabel != null) descLabel.setText(task.getDescription());
        if (dueDateLabel != null) dueDateLabel.setText(task.getDueDate() != null ? "Due: " + task.getDueDate() : "No due date");
        priorityLabel.setText(task.getPriority().name());
        completedCheck.setSelected(task.isCompleted());

        // 🛑 FIX: REMOVE INLINE STYLES, USE CSS CLASSES FOR PRIORITY COLORS
        priorityLabel.getStyleClass().removeAll("high-priority", "medium-priority", "low-priority");
        cardContainer.getStyleClass().remove("completed-card"); // Clean up for potential completion styling

        switch (task.getPriority()) {
            case HIGH -> priorityLabel.getStyleClass().add("high-priority");
            case MEDIUM -> priorityLabel.getStyleClass().add("medium-priority");
            case LOW -> priorityLabel.getStyleClass().add("low-priority");
        }

        // Add a class to the card if completed (for visual greying out/fading)
        if (task.isCompleted()) {
            cardContainer.getStyleClass().add("completed-card");
        }


        // 🧩 Checkbox toggle
        completedCheck.setOnAction(e -> {
            task.setCompleted(completedCheck.isSelected());
            taskService.updateTask(task);
            onListChanged.accept(null);
        });

        // ✏️ Edit
        editBtn.setOnAction(e -> {
            try {
                // 🛑 FIX: Load the correct FORM FXML, not the CARD FXML
                // And use the reliable MainApp.class relative path
                FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("task-form-view.fxml"));
                Scene scene = new Scene(loader.load());

                scene.getStylesheets().addAll(editBtn.getScene().getStylesheets());

                // Get the correct controller (this will work now)
                TaskFormController controller = loader.getController();

                // 🛑 CRITICAL: Inject service, the task to edit, and the callback
                controller.setTaskService(taskService);
                controller.setExistingTask(task); // Pass the current task
                controller.setOnSaveCallback(() -> onListChanged.accept(null));

                // Configure and show the new window (Stage)
                Stage stage = new Stage();
                stage.setTitle("Edit Task");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(editBtn.getScene().getWindow());
                stage.setScene(scene);
                stage.showAndWait();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        // 🗑️ Delete
        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Task");
            confirm.setHeaderText("Are you sure you want to delete this?");
            confirm.setContentText(task.getTitle());
            confirm.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    taskService.deleteTask(task.getId());
                    onListChanged.accept(null);
                }
            });
        });

        setText(null);
        setGraphic(cardContainer);
    }
}