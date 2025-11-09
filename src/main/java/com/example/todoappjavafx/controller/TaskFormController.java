package com.example.todoappjavafx.controller;

import com.example.todoappjavafx.model.Priority;
import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.service.TaskService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class TaskFormController {

    @FXML private Label formTitle;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<Priority> priorityBox;
    @FXML private DatePicker dueDatePicker;
    @FXML private CheckBox completedCheck;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private TaskService taskService;
    private Task existingTask;
    private Runnable onSaveCallback;

    @FXML
    public void initialize() {
        // populate priority options
        priorityBox.getItems().setAll(Priority.values());
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setExistingTask(Task task) {
        this.existingTask = task;
        formTitle.setText("Edit Task");

        // prefill fields for editing
        titleField.setText(task.getTitle());
        descriptionField.setText(task.getDescription());
        priorityBox.setValue(task.getPriority());
        dueDatePicker.setValue(task.getDueDate());
        completedCheck.setSelected(task.isCompleted());
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void onSave() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Title cannot be empty!").show();
            return;
        }

        String description = descriptionField.getText();
        Priority priority = priorityBox.getValue() != null ? priorityBox.getValue() : Priority.MEDIUM;
        LocalDate dueDate = dueDatePicker.getValue();
        boolean completed = completedCheck.isSelected();

        if (existingTask == null) {
            Task newTask = new Task(title, description, priority, dueDate);
            newTask.setCompleted(completed);
            taskService.addTask(newTask);
        } else {
            existingTask.setTitle(title);
            existingTask.setDescription(description);
            existingTask.setPriority(priority);
            existingTask.setDueDate(dueDate);
            existingTask.setCompleted(completed);
            taskService.updateTask(existingTask);
        }

        if (onSaveCallback != null) onSaveCallback.run();

        closeForm();
    }

    @FXML
    private void onCancel() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}
