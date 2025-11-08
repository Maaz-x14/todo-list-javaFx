package com.example.todoappjavafx.view;

import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.service.TaskService;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import java.io.IOException;
import java.util.function.Consumer;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;


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
            TextInputDialog dialog = new TextInputDialog(task.getTitle());
            dialog.setTitle("Edit Task");
            dialog.setHeaderText("Update task title");
            dialog.setContentText("New title:");
            dialog.showAndWait().ifPresent(newTitle -> {
                task.setTitle(newTitle);
                taskService.updateTask(task);
                onListChanged.accept(null);
            });
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

        // ✨ Hover animations (already correct)
        cardContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> animateHover(true));
        cardContainer.addEventHandler(MouseEvent.MOUSE_EXITED, e -> animateHover(false));

        setText(null);
        setGraphic(cardContainer);
    }

    private void animateHover(boolean hover) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), cardContainer);
        st.setToX(hover ? 1.03 : 1.0);
        st.setToY(hover ? 1.03 : 1.0);
        st.play();

        // The card's hover style is already correct and uses -fx-background and -fx-base
        cardContainer.setStyle(hover
                ? "-fx-background-color: derive(-fx-base, 20%); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
                : "-fx-background-color: -fx-background;");
    }
}