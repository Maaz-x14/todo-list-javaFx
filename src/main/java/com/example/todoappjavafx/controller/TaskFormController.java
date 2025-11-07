package com.example.todoappjavafx.controller;

import com.example.todoappjavafx.model.Priority;
import com.example.todoappjavafx.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.UUID;

public class TaskFormController {

    @FXML private Label formTitle;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<Priority> priorityBox;
    @FXML private DatePicker dueDatePicker;
    @FXML private CheckBox completedCheck;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private Task task;
    private boolean isEditMode = false;
    private TaskFormListener listener;

    @FXML
    public void initialize() {
        priorityBox.getItems().addAll(Priority.values());
    }

    public void setTaskFormListener(TaskFormListener listener) {
        this.listener = listener;
    }

    public void setEditMode(Task task) {
        this.task = task;
        this.isEditMode = true;

        formTitle.setText("Edit Task");
        titleField.setText(task.getTitle());
        descriptionField.setText(task.getDescription());
        priorityBox.setValue(task.getPriority());
        dueDatePicker.setValue(task.getDueDate());
        completedCheck.setSelected(task.isCompleted());
    }

    @FXML
    private void onSave() {
        String title = titleField.getText();
        String desc = descriptionField.getText();
        Priority priority = priorityBox.getValue();
        LocalDate due = dueDatePicker.getValue();
        boolean completed = completedCheck.isSelected();

        if (title.isBlank() || priority == null) {
            showAlert("Validation Error", "Please fill in all required fields.");
            return;
        }

        if (isEditMode && task != null) {
            task.setTitle(title);
            task.setDescription(desc);
            task.setPriority(priority);
            task.setDueDate(due);
            task.setCompleted(completed);
        } else {
            task = new Task(
                    UUID.randomUUID().toString(),
                    title,
                    desc,
                    priority,
                    due,
                    completed
            );
        }

        if (listener != null)
            listener.onTaskSaved(task);

        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public interface TaskFormListener {
        void onTaskSaved(Task task);
    }
}
