package com.example.todoappjavafx.controller;

import com.example.todoappjavafx.model.Priority;
import com.example.todoappjavafx.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class NewTaskController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<Priority> priorityCombo;
    @FXML private DatePicker dueDatePicker;

    private Task newTask;

    private Task currentTask;

    @FXML
    public void initialize() {
        // Populate priority options from enum
        priorityCombo.getItems().setAll(Priority.values());
        priorityCombo.setValue(Priority.MEDIUM);

        // Default date = today
        dueDatePicker.setValue(LocalDate.now());
    }

    /** Called when the Save button is pressed */
    @FXML
    private void handleSave() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        Priority priority = priorityCombo.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (title == null || title.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Missing Title");
            alert.setContentText("Please enter a task title before saving.");
            alert.showAndWait();
            return;
        }

        // Create a new Task instance
        newTask = new Task(
                title.trim(),
                description != null ? description.trim() : "",
                priority,
                dueDate
        );

        // Close the dialog window
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();

        this.currentTask.setTitle(titleField.getText().trim());
        this.currentTask.setDescription(descriptionField.getText().trim());
        this.currentTask.setPriority(priorityCombo.getValue());
        this.currentTask.setDueDate(dueDatePicker.getValue());
    }

    /** Called when the Cancel button is pressed */
    @FXML
    private void handleCancel() {
        newTask = null;
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    /** Returns the created Task (or null if canceled) */
    public Task getNewTask() {
        return newTask;
    }

    public void setTask(Task taskToEdit) {
        if (taskToEdit != null) {
            // EDIT MODE: Load data from the existing task
            this.currentTask = taskToEdit;
            titleField.setText(taskToEdit.getTitle());
            descriptionField.setText(taskToEdit.getDescription());
            priorityCombo.setValue(taskToEdit.getPriority());
            dueDatePicker.setValue(taskToEdit.getDueDate());
        } else {
            // CREATE MODE: Initialize a new, temporary Task object
            this.currentTask = new Task("", "", Priority.MEDIUM, LocalDate.now());
        }
    }

    public Task getResultTask() {
        return this.currentTask; // Returns the updated object or null
    }
}
